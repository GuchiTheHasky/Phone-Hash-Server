package org.the.husky;

import io.javalin.Javalin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebServer {
    private final Map<String, RedisNodeClient> nodes;  private final HashingService hashing;
    private final ExecutorService smallPool = Executors.newFixedThreadPool(4);

    public WebServer(Map<String,RedisNodeClient> nodes,HashingService hashing){this.nodes=nodes;this.hashing=hashing;}

    public void start(){
        Javalin app = Javalin.create().start(8080);

        // /hash/{phone}  → calculate + return hash
        app.get("/hash/{phone}", ctx->{
            String phone = ctx.pathParam("phone");
            String hash  = hashing.hash(phone);                // ★ CHANGED: no Redis needed
            ctx.json(Map.of("phone",phone,"hash",hash));
        });

        // /phone/{hash} → parallel GET on 4 nodes
        app.get("/phone/{hash}", ctx->{
            String hash = ctx.pathParam("hash");
            List<Callable<String>> tasks = nodes.values().stream()
                    .<Callable<String>>map(node -> ()->node.findPhoneByHash(hash)).toList();
            String phone = null;
            for(Future<String> f : smallPool.invokeAll(tasks)){
                String p=f.get(); if(p!=null){ phone=p; break; }
            }
            if(phone==null) ctx.status(404).result("Not found");
            else            ctx.json(Map.of("phone",phone,"hash",hash));
        });
    }
}

