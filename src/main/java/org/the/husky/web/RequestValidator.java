package org.the.husky.web;

import org.the.husky.config.Config;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestValidator {
    private static final String AUTH_TOKEN_PREFIX = "Basic ";
    private final Logger logger = Logger.getLogger(RequestValidator.class.getName());

    private final Config config;

    public RequestValidator(Config config) {
        this.config = config;
    }

    public boolean isNotAuthorized(String authHeader) {
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

    public boolean isInvalidPhoneNumber(String phone) {
        if (phone == null) {
            return true;
        }

        if (phone.startsWith("+")) {
            phone = phone.substring(1);
        }

        return !phone.matches("^380\\d{9}$");
    }

    public boolean isInvalidHash(String hash) {
        if (hash == null || !hash.matches("^[a-fA-F0-9]+$")) {
            return true;
        }
        return hash.length() != getExpectedHashLength();
    }


    private int getExpectedHashLength() {
        return switch (config.getHashAlgorithm().toUpperCase()) {
            case "SHA-1"      -> 40;
            case "SHA-256"    -> 64;
            case "SHA-384"    -> 96;
            case "SHA-512"    -> 128;
            case "SHA3-256"   -> 64;
            case "SHA3-512"   -> 128;
            case "MD5"        -> 32;
            case "MD6"        -> 64;
            default           -> 64;
        };
    }
}
