package org.the.husky.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashGenerator {
    private static String salt;
    private static ThreadLocal<MessageDigest> digest;

    public static void init(String algorithm, String inputSalt) {
        salt = inputSalt;
        digest = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance(algorithm);
            } catch (Exception e) {
                throw new RuntimeException("Unsupported hash algorithm: " + algorithm, e);
            }
        });
    }

    public static String generate(String input) {
        MessageDigest d = digest.get();
        d.reset();
        byte[] bytes = d.digest((input + salt).getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}



