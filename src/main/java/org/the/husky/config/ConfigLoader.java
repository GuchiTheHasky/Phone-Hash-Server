package org.the.husky.config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigLoader {

    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());

    public static Config load() {
        Properties properties = new Properties();
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(in);

            logger.info("Loaded application properties");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return new Config.Builder()
                .serverPort(Integer.parseInt(properties.getProperty("server.port")))
                .hashAlgorithm(properties.getProperty("hash.algorithm"))
                .salt(properties.getProperty("salt"))
                .numbersPerPrefix(Integer.parseInt(properties.getProperty("numbersPerPrefix")))
                .batchSize(Integer.parseInt(properties.getProperty("batch.size")))
                .redisHost(properties.getProperty("redis.host"))
                .redisPort(Integer.parseInt(properties.getProperty("redis.port")))
                .userName(properties.getProperty("user.name"))
                .password(properties.getProperty("password"))
                .poolSize(Integer.parseInt(properties.getProperty("pool.size")))
                .codes(Arrays.asList(properties.getProperty("phone.codes").split(",")))
                .build();
    }
}
