package org.the.husky.service.impl;

import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.service.PreloadService;
import org.the.husky.util.HashGenerator;
import org.the.husky.util.PhoneNumberGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class PreloadServiceImpl implements PreloadService {
    private static final Logger logger = Logger.getLogger(PreloadServiceImpl.class.getName());

    private final RedisNodeClient client;
    private final Config config;

    public PreloadServiceImpl(RedisNodeClient client, Config config) {
        this.client = client;
        this.config = config;
    }

    @Override
    public void preload() throws InterruptedException {
        List<String> phoneCodes = config.getCodes();
        ExecutorService pool = Executors.newFixedThreadPool(config.getPoolSize());

        for (String code : phoneCodes) {
            pool.submit(() -> preloadCode(code));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);
    }

    private void preloadCode(String code) {
        PhoneNumberGenerator gen = new PhoneNumberGenerator(code, config.getNumbersPerPrefix());

        Map<String, String> batch = new HashMap<>(config.getBatchSize());
        int count = 0;

        while (gen.hasNext()) {
            String phone = gen.next();
            String hash = HashGenerator.generate(phone);
            batch.put(phone, hash);
            if (batch.size() == config.getBatchSize()) {
                client.putBatch(batch);
                batch.clear();
            }
            if (++count % 500_000 == 0) {
                logger.info("code: %s processed: %s thread: %s".formatted(code, count, Thread.currentThread().getName()));
            }
        }

        if (!batch.isEmpty()) client.putBatch(batch);
    }
}
