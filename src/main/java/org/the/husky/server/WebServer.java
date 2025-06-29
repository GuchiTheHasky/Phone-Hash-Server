package org.the.husky.server;

import io.javalin.Javalin;
import org.the.husky.config.Config;
import org.the.husky.service.HashingService;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.util.JsonGenerator;

import static org.the.husky.constant.Constants.APPLICATION_JSON;

public class WebServer {
    private final RedisNodeClient node;
    private final HashingService hashing;
    private final Config config;

    public WebServer(RedisNodeClient node, HashingService hashing, Config config) {
        this.node = node;
        this.hashing = hashing;
        this.config = config;
    }

    public void start() {
        Javalin app = Javalin.create().start(config.getServerPort());

        app.get("/hash/{phone}", ctx -> {
            String phone = ctx.pathParam("phone");
            String hash = hashing.hash(phone);
            String json = JsonGenerator.successResponse(hash);
            ctx.result(json).contentType(APPLICATION_JSON);
        });

        app.get("/phone/{hash}", ctx -> {

            String hash = ctx.pathParam("hash");
            String phone = node.findPhoneByHash(hash);

            if (phone == null) {
                ctx.status(404).result(JsonGenerator.errorResponse()).contentType(APPLICATION_JSON);
            } else {
                String json = JsonGenerator.successResponse(phone);
                ctx.result(json).contentType(APPLICATION_JSON);
            }

        });
    }
}

