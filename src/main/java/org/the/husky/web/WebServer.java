package org.the.husky.web;

import io.javalin.Javalin;
import org.the.husky.config.Config;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.util.HashGenerator;

import java.util.Map;
import java.util.logging.Logger;


public class WebServer {
    private static final Logger logger = Logger.getLogger(WebServer.class.getName());
    private static final String AUTH_HEADER = "Authorization";
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ERROR = "Error";

    private final RedisNodeClient node;
    private final Config config;
    private final RequestValidator requestValidator;

    public WebServer(RedisNodeClient node, RequestValidator validator, Config config) {
        this.node = node;
        this.requestValidator = validator;
        this.config = config;
    }

    public void start() {
        Javalin app = Javalin.create().start(config.getServerPort());

        getHashByPhoneNumber(app);
        getPhoneByHash(app);
    }

    private void getPhoneByHash(Javalin app) {
        app.get("/phone", context -> {

            String auth = context.header(AUTH_HEADER);
            if (requestValidator.isNotAuthorized(auth)) {
                context.status(401)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, UNAUTHORIZED));
                return;
            }

            String hash = context.queryParam("hash");

            if (requestValidator.isInvalidHash(hash)) {
                context.status(400)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, "Bad request, invalid hash"));
                return;
            }
            logger.info("phone request: ");

            String phone = node.findPhoneByHash(hash);

            context.status(200)
                    .contentType(APPLICATION_JSON)
                    .json(Map.of("phone", phone));
        });
    }

    private void getHashByPhoneNumber(Javalin app) {
        app.get("/hash", context -> {

            String auth = context.header(AUTH_HEADER);
            if (requestValidator.isNotAuthorized(auth)) {
                context.status(401)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, UNAUTHORIZED));
                return;
            }

            String phone = context.queryParam("phone");

            if (requestValidator.isInvalidPhoneNumber(phone)) {
                context.status(400)
                        .contentType(APPLICATION_JSON)
                        .json(Map.of(ERROR, "Bad request, invalid phone number"));
                return;
            }

            logger.info("hash request: ");


            String hash = HashGenerator.generate(phone);
            context.status(200)
                    .contentType(APPLICATION_JSON)
                    .json(Map.of("hash", hash));
        });
    }

}