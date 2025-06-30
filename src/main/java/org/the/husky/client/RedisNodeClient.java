package org.the.husky.client;

import org.the.husky.config.Config;
import redis.clients.jedis.Jedis;

import java.util.Map;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class RedisNodeClient {
    private final JedisPool jedisPool;

    public RedisNodeClient(Config config) {
        this.jedisPool = new JedisPool(config.getRedisHost(), config.getRedisPort());
    }

    public void putBatch(Map<String, String> phoneToHash) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            phoneToHash.forEach((phone, hash) -> pipeline.set("h:" + hash, phone));
            pipeline.sync();
        }
    }

    public String findPhoneByHash(String hash) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("h:" + hash);
        }
    }
}


