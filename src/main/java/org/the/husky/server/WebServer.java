package org.the.husky.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.the.husky.config.Config;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.util.HashGenerator;
import org.the.husky.util.JsonGenerator;

import static org.the.husky.constant.Constants.APPLICATION_JSON;

public class WebServer {
    private final RedisNodeClient node;
    private final Config config;

    public WebServer(RedisNodeClient node, Config config) {
        this.node = node;
        this.config = config;
    }

    public void start() {
        Javalin app = Javalin.create().start(config.getServerPort());

        app.post("/auth", ctx -> {
            String username = config.getUsername();
            String password = config.getPassword();
            String token = HashGenerator.generate(username + ":" + password);
            ctx.result(JsonGenerator.successResponse(token)).contentType(APPLICATION_JSON);
        });

        app.get("/hash/{phone}", ctx -> {

            if (isNotAuthorised(ctx)) {
                String json = JsonGenerator.notAuthorizedResponse();
                ctx.status(401).result(json).contentType(APPLICATION_JSON);
                return;
            }

            String phone = ctx.pathParam("phone");
            String hash = HashGenerator.generate(phone);
            String json = JsonGenerator.successResponse(hash);
            ctx.result(json).contentType(APPLICATION_JSON);
        });

        app.get("/phone/{hash}", ctx -> {

            if (isNotAuthorised(ctx)) {
                String json = JsonGenerator.notAuthorizedResponse();
                ctx.status(401).result(json).contentType(APPLICATION_JSON);
                return;
            }

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

    private boolean isNotAuthorised(Context ctx) {
        String header = ctx.header("Authorization");
        String expectedToken = HashGenerator.generate(config.getUsername() + ":" + config.getPassword());

        return header == null || !header.equals(expectedToken);
    }
}

