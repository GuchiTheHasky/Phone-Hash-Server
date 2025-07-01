package org.the.husky.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.the.husky.config.Config;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.util.HashGenerator;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WebServer {
    private static final Logger logger = Logger.getLogger(WebServer.class.getName());
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN_PREFIX = "Basic ";
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ERROR = "Error";

    private final RedisNodeClient node;
    private final Config config;

    public WebServer(RedisNodeClient node, Config config) {
        this.node = node;
        this.config = config;
    }

    public void start() {
        Javalin app = Javalin.create().start(config.getServerPort());

        getHashByPhoneNumber(app);
        getPhoneByHash(app);
        healthCheck(app);
    }

    private void healthCheck(Javalin app) {
        app.get("/health", ctx -> ctx.result("OK"));
    }

    private void getPhoneByHash(Javalin app) {
        app.get("/phone", context -> {

            String auth = context.header(AUTH_HEADER);
            if (isNotAuthorized(auth)) {
                context.status(401)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, UNAUTHORIZED));
                return;
            }

            String hash = context.queryParam("hash");
            String phone = node.findPhoneByHash(hash);

            context.status(200)
                    .contentType(APPLICATION_JSON)
                    .json(Map.of("phone", phone));
        });
    }

    private void getHashByPhoneNumber(Javalin app) {
        app.get("/hash", context -> {

            String auth = context.header(AUTH_HEADER);
            if (isNotAuthorized(auth)) {
                context.status(401)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, UNAUTHORIZED));
                return;
            }

            String phone = context.queryParam("phone");
            String hash = HashGenerator.generate(phone);
            context.status(200)
                    .contentType(APPLICATION_JSON)
                    .json(Map.of("hash", hash));
        });
    }

    private boolean isNotAuthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(AUTH_TOKEN_PREFIX)) {
            return true;
        }

        try {
            String base64Credentials = authHeader.substring(AUTH_TOKEN_PREFIX.length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decoded = new String(decodedBytes);
            String expected = config.getUsername() + ":" + config.getPassword();
            return !decoded.equals(expected);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to decode Authorization header", e);
            return true;
        }
    }
}