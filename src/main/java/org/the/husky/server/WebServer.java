package org.the.husky.server;

import io.javalin.Javalin;
import org.the.husky.service.impl.HashingServiceImpl;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.mapper.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebServer {
    private final Map<String, RedisNodeClient> nodes;
    private final HashingServiceImpl hashing;
    private final JsonMapper jsonMapper;

    private final ExecutorService smallPool = Executors.newFixedThreadPool(4);

    public WebServer(Map<String, RedisNodeClient> nodes, HashingServiceImpl hashing, JsonMapper jsonMapper) {
        this.nodes = nodes;
        this.hashing = hashing;
        this.jsonMapper = jsonMapper;
    }

    public void start() {
        Javalin app = Javalin.create().start(8080);

        // /hash/{phone}  → calculate + return hash
        app.get("/hash/{phone}", ctx -> {
            String phone = ctx.pathParam("phone");
            String hash = hashing.hash(phone);                // ★ CHANGED: no Redis needed
            String json = jsonMapper.toHashJson(hash);
            ctx.result(json).contentType("application/json");
        });

        app.get("/phone/{hash}", ctx -> {

            String hash = ctx.pathParam("hash");
            List<Callable<String>> tasks = nodes.values().stream()
                    .<Callable<String>>map(node -> () -> node.findPhoneByHash(hash)).toList();

            String phone = null;
            for (Future<String> f : smallPool.invokeAll(tasks)) {
                String p = f.get();
                if (p != null) {
                    phone = p;
                    break;
                }
            }

            if (phone == null) {
                ctx.status(404).result("{\"error\":\"Not found\"}").contentType("application/json");
            } else {
                String json = jsonMapper.toPhoneJson(phone);
                ctx.result(json).contentType("application/json");
            }

        });
    }
}

