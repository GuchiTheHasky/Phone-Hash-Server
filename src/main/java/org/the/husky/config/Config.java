package org.the.husky.config;

public class Config {
//    public static final String HASH_ALGORITHM = "SHA3-256";
//    public static final String SALT = "mySecretSalt";
//    public static final int NUMBERS_PER_PREFIX = 12_500_000;

    private final String hashAlgorithm;
    private final String salt;
    private final int numbersPerPrefix;

    private Config(Builder builder) {
        this.hashAlgorithm = builder.hashAlgorithm;
        this.salt = builder.salt;
        this.numbersPerPrefix = builder.numbersPerPrefix;
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

    public static class Builder {
        private String hashAlgorithm;
        private String salt;
        private int numbersPerPrefix;

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

        public Config build() {
            return new Config(this);
        }
    }
}