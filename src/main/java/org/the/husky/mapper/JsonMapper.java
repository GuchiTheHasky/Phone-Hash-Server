package org.the.husky.mapper;

public class JsonMapper { // todo: rename it

    public String toPhoneJson(String phone) {
        return "{\"phone\":\"" + phone + "\"}";
    }

    public String toHashJson(String hash) {
        return "{\"hash\":\"" + hash + "\"}";
    }

    public String notFoundJson() {
        return "{\"error\":\"Not found\"}";
    }
}
