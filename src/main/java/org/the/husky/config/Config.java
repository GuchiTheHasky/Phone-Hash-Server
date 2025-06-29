package org.the.husky.config;

public class Config {
    private final int serverPort;
    private final String hashAlgorithm;
    private final String salt;
    private final int numbersPerPrefix;
    private final int batchSize;
    private final String redisHost;
    private final int redisPort;

    private Config(Builder builder) {
        this.serverPort = builder.serverPort;
        this.hashAlgorithm = builder.hashAlgorithm;
        this.salt = builder.salt;
        this.numbersPerPrefix = builder.numbersPerPrefix;
        this.batchSize = builder.batchSize;
        this.redisHost = builder.redisHost;
        this.redisPort = builder.redisPort;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getSalt() {
        return salt;
    }

    public int getNumbersPerPrefix() {
        return numbersPerPrefix;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public static class Builder {
        private int serverPort;
        private String hashAlgorithm;
        private String salt;
        private int numbersPerPrefix;
        private int batchSize;
        private String redisHost;
        private int redisPort;

        public Builder serverPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public Builder hashAlgorithm(String hashAlgorithm) {
            this.hashAlgorithm = hashAlgorithm;
            return this;
        }

        public Builder salt(String salt) {
            this.salt = salt;
            return this;
        }

        public Builder numbersPerPrefix(int numbersPerPrefix) {
            this.numbersPerPrefix = numbersPerPrefix;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder redisHost(String redisHost) {
            this.redisHost = redisHost;
            return this;
        }

        public Builder redisPort(int redisPort) {
            this.redisPort = redisPort;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}