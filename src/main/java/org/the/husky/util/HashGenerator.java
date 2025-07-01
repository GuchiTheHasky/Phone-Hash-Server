package org.the.husky.util;

import org.the.husky.config.Config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashGenerator {
    private static String salt;
    private static ThreadLocal<MessageDigest> digest;

    public static void init(Config config) {
        salt = config.getSalt();
        digest = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance(config.getHashAlgorithm());
            } catch (Exception e) {
                throw new RuntimeException("Unsupported hash algorithm: " + config.getHashAlgorithm(), e);
            }
        });
    }

    public static String generate(String input) {
        MessageDigest messageDigest = digest.get();
        messageDigest.reset();

        String inputWithSalt = input + salt;
        byte[] hashBytes = messageDigest.digest((inputWithSalt).getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            builder.append(String.format("%02x", hashByte));
        }
        return builder.toString();
    }
}



