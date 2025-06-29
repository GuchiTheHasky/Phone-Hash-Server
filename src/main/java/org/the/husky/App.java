package org.the.husky;


import org.the.husky.config.Config;
import org.the.husky.config.ConfigLoader;
import org.the.husky.mapper.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) throws Exception {
        Config configuration = ConfigLoader.load();

        Map<String,RedisNodeClient> clients = Map.of(
                "38067", new RedisNodeClient("redis://redis1:6379"),
                "38068", new RedisNodeClient("redis://redis2:6379"),
                "38097", new RedisNodeClient("redis://redis3:6379"),
                "38098", new RedisNodeClient("redis://redis4:6379"));

        ExecutorService preloadPool = Executors.newFixedThreadPool(4);
        clients.forEach((prefix,client) -> preloadPool.submit(() -> {
            PhoneNumberGenerator gen = new PhoneNumberGenerator(prefix, configuration.getNumbersPerPrefix());
            HashingService hashing = new HashingService(configuration.getHashAlgorithm(), configuration.getSalt());
            Map<String,String> batch = new HashMap<>(1024);
            int count=0;
            while(gen.hasNext()){
                String phone=gen.next(); String hash=hashing.hash(phone);
                batch.put(phone,hash); if(batch.size()==1000){client.putBatch(batch);batch.clear();}
                if(++count%1_000_000==0) System.out.println("✔ "+prefix+": "+count);
            }
            if(!batch.isEmpty()) client.putBatch(batch);
        }));
        preloadPool.shutdown(); preloadPool.awaitTermination(30,TimeUnit.MINUTES);

        // start REST API
        new WebServer(clients,new HashingService(configuration.getHashAlgorithm(), configuration.getSalt()), new JsonMapper()).start();
        System.out.println("SERVICE WORK ON HOST http://localhost:8080 (ready)");
    }
}