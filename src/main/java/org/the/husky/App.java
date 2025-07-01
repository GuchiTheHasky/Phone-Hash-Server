package org.the.husky;

import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.config.ConfigLoader;
import org.the.husky.server.WebServer;
import org.the.husky.service.PreloadService;
import org.the.husky.service.impl.PreloadServiceImpl;
import org.the.husky.util.HashGenerator;

public class App {

    public static void main(String[] args) throws Exception {

        Config config = ConfigLoader.load();
        HashGenerator.init(config);

        RedisNodeClient redisClient = new RedisNodeClient(config);

        PreloadService preloadService = new PreloadServiceImpl(redisClient, config);
        preloadService.preload();

        new WebServer(redisClient, config).start();
    }
}