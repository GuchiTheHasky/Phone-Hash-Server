package org.the.husky.mapper;

public class JsonMapper {

    public String toPhoneJson(String phone) {
        return "{\"phone\":\"" + phone + "\"}";
    }

    public String toHashJson(String hash) {
        return "{\"hash\":\"" + hash + "\"}";
    }
}
