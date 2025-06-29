package org.the.husky.client;

import redis.clients.jedis.Jedis;

import java.util.Map;

import java.net.InetSocketAddress;
import java.net.Socket;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;

public class RedisNodeClient {
    private final JedisPool jedisPool;

    public RedisNodeClient(String redisUrl) {
        String host = redisUrl.split("//")[1].split(":")[0];
        int port = Integer.parseInt(redisUrl.split(":")[2]);

        waitUntilRedisReady(host, port);
        this.jedisPool = new JedisPool(host, port);
    }

    // 🔁 PUT batch using pipelining
    public void putBatch(Map<String, String> phoneToHash) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            phoneToHash.forEach((phone, hash) -> pipeline.set("h:" + hash, phone));
            pipeline.sync();
        }
    }

    // 🔍 GET phone by hash
    public String findPhoneByHash(String hash) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("h:" + hash);
        }
    }

    // ⏳ Wait until Redis is ready
    private void waitUntilRedisReady(String host, int port) {
        for (int i = 0; i < 60; i++) {
            if (isRedisReady(host, port)) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Redis", e);
            }
        }
        throw new RuntimeException("Redis " + host + ":" + port + " not ready after timeout");
    }

    // ✅ Lightweight PING
    private boolean isRedisReady(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 500);
            socket.getOutputStream().write("PING\r\n".getBytes());
            byte[] buffer = new byte[128];
            int read = socket.getInputStream().read(buffer);
            return new String(buffer, 0, read).contains("PONG");
        } catch (IOException e) {
            return false;
        }
    }
}



//public class RedisNodeClient {
//    private final RedissonClient redisson;
//
//    public RedisNodeClient(String redisUrl) {
//        waitUntilRedisReady(redisUrl);
//        this.redisson = createClient(redisUrl);
//    }
//
//    public void putBatch(Map<String, String> phoneToHash) {
//        RBatch batch = redisson.createBatch();
//        phoneToHash.forEach((phone, hash) -> batch.getBucket("h:" + hash).setAsync(phone));
//        batch.execute();
//    }
//
//    public String findPhoneByHash(String hash) {
//        Object v = redisson.getBucket("h:" + hash).get();
//        return v == null ? null : v.toString();
//    }
//
//    private RedissonClient createClient(String redisUrl) {
//        Config config = new Config();
//        config.useSingleServer().setAddress(redisUrl);
//        return Redisson.create(config);
//    }
//
//    private void waitUntilRedisReady(String redisUrl) {
//        String host = redisUrl.split("//")[1].split(":")[0];
//        int port = Integer.parseInt(redisUrl.split(":")[2]);
//
//        for (int i = 0; i < 60; i++) {
//            if (isRedisReady(host, port)) return;
//            try { Thread.sleep(1000); } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new RuntimeException("Interrupted while waiting for Redis", e);
//            }
//        }
//        throw new RuntimeException("Redis " + redisUrl + " not ready after timeout");
//    }
//
//    private boolean isRedisReady(String host, int port) {
//        try (Socket socket = new Socket()) {
//            socket.connect(new InetSocketAddress(host, port), 500);
//            socket.getOutputStream().write("PING\r\n".getBytes());
//            byte[] buffer = new byte[128];
//            int read = socket.getInputStream().read(buffer);
//            return new String(buffer, 0, read).contains("PONG");
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}

