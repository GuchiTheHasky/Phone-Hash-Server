package org.the.husky;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashingService {
    private final MessageDigest digest;
    private final String salt;

    public HashingService(String algorithm, String salt) {
        try { this.digest = MessageDigest.getInstance(algorithm); }
        catch (Exception e) { throw new RuntimeException("No such algorithm: " + algorithm); }
        this.salt = salt;
    }

    public String hash(String input) {
        byte[] bytes = digest.digest((input + salt).getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
