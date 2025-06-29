package org.the.husky.config;

import java.io.InputStream;
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
                .hashAlgorithm(properties.getProperty("hash.algorithm"))
                .salt(properties.getProperty("salt"))
                .numbersPerPrefix(Integer.parseInt(properties.getProperty("numbersPerPrefix")))
                .build();
    }
}
