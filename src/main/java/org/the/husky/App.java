package org.the.husky;


import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.config.ConfigLoader;
import org.the.husky.mapper.JsonMapper;
import org.the.husky.server.WebServer;
import org.the.husky.service.impl.HashingServiceImpl;
import org.the.husky.util.PhoneNumberGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.the.husky.constant.Constants.*;

public class App {
    public static void main(String[] args) throws Exception {
        Config configuration = ConfigLoader.load();

        Map<String, RedisNodeClient> clients = Map.of(
                CODE_067, new RedisNodeClient("redis://redis_067:6379"),
                CODE_068, new RedisNodeClient("redis://redis_068:6379"),
                CODE_097, new RedisNodeClient("redis://redis_097:6379"),
                CODE_098, new RedisNodeClient("redis://redis_098:6379"),
                CODE_096, new RedisNodeClient("redis://redis_096:6379"),
                CODE_077, new RedisNodeClient("redis://redis_077:6379"));

        ExecutorService preloadPool = Executors.newFixedThreadPool(6);

        clients.forEach((prefix, client) -> preloadPool.submit(() -> {
            PhoneNumberGenerator gen = new PhoneNumberGenerator(prefix, configuration.getNumbersPerPrefix());
            HashingServiceImpl hashing = new HashingServiceImpl(configuration.getHashAlgorithm(), configuration.getSalt());
            Map<String, String> batch = new HashMap<>(1024);
            int count = 0;
            while (gen.hasNext()) {
                String phone = gen.next();
                String hash = hashing.hash(phone);
                batch.put(phone, hash);
                if (batch.size() == 1000) {
                    client.putBatch(batch);
                    batch.clear();
                }
                if (++count % 500_000 == 0) System.out.println("✔ " + prefix + ": " + count);
            }
            if (!batch.isEmpty()) client.putBatch(batch);
        }));

        preloadPool.shutdown();
        preloadPool.awaitTermination(30, TimeUnit.MINUTES);

        // start REST API
        new WebServer(clients, new HashingServiceImpl(configuration.getHashAlgorithm(), configuration.getSalt()), new JsonMapper()).start();
        System.out.println("SERVICE WORK ON HOST http://localhost:8080 (ready)");
    }
}