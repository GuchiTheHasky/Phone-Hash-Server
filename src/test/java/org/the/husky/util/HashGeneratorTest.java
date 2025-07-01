package org.the.husky.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.the.husky.config.Config;


public class HashGeneratorTest {

    private Config config;

    @BeforeEach
    void setup() {
        config = mock(Config.class);
        when(config.getHashAlgorithm()).thenReturn("SHA-256");
        when(config.getSalt()).thenReturn("theHuskySalt");
        HashGenerator.init(config);
    }

    @Test
    void shouldGenerateSameHashForSamePhoneNumbers() {
        String input = "380671231231";
        String hash1 = HashGenerator.generate(input);
        String hash2 = HashGenerator.generate(input);

        assertEquals(hash1, hash2);
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentPhoneNumbers() {
        String hash1 = HashGenerator.generate("380671231231");
        String hash2 = HashGenerator.generate("380971231231");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentSalt() {
        String input = "380671231231";
        String hash1 = HashGenerator.generate(input);

        when(config.getSalt()).thenReturn("salt");

        HashGenerator.init(config);
        String hash2 = HashGenerator.generate(input);

        assertNotEquals(hash1, hash2);
    }
}
