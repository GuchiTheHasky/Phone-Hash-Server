package org.the.husky.service.impl;

import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.service.PreloadService;
import org.the.husky.util.PhoneNumberGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.the.husky.constant.Constants.*;

public class PreloadServiceImpl implements PreloadService {
    private final RedisNodeClient client;
    private final Config config;
    private final Logger logger = Logger.getLogger(PreloadServiceImpl.class.getName());

    public PreloadServiceImpl(RedisNodeClient client, Config config) {
        this.client = client;
        this.config = config;
    }

    @Override
    public void preload() throws InterruptedException {
        List<String> phoneCodes = List.of(CODE_067, CODE_068, CODE_077, CODE_096, CODE_097, CODE_098);
        ExecutorService pool = Executors.newFixedThreadPool(phoneCodes.size());

        for (String code : phoneCodes) {
            pool.submit(() -> preloadCode(code));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);
    }

    private void preloadCode(String code) {
        PhoneNumberGenerator gen = new PhoneNumberGenerator(code, config.getNumbersPerPrefix());
        HashingServiceImpl hashing = new HashingServiceImpl(config.getHashAlgorithm(), config.getSalt());

        Map<String, String> batch = new HashMap<>(config.getBatchSize());
        int count = 0;

        while (gen.hasNext()) {
            String phone = gen.next();
            String hash = hashing.hash(phone);
            batch.put(phone, hash);
            if (batch.size() == config.getBatchSize()) {
                client.putBatch(batch);
                batch.clear();
            }
            if (++count % 500_000 == 0) {
                logger.info("code: %s count: %s processed".formatted(code, count));
            }
        }

        if (!batch.isEmpty()) client.putBatch(batch);
    }
}
