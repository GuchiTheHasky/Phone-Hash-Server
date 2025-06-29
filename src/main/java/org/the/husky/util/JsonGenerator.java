package org.the.husky.util;

public class JsonGenerator {

    public static String successResponse(String content) {
        if (content.length() == 12) {
            return generatePhoneJson(content);
        }
        return generateHashJson(content);
    }

    public static String errorResponse() {
        return "{\"error\": \"Not found\"}";
    }

    private static String generatePhoneJson(String phone) {
        return "{\"phone\":\"" + phone + "\"}";
    }

    private static String generateHashJson(String hash) {
        return "{\"hash\":\"" + hash + "\"}";
    }
}
