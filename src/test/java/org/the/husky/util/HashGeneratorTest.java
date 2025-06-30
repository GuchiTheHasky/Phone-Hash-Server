package org.the.husky.util;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class HashGeneratorTest {

    @BeforeAll
    static void setup() {
        HashGenerator.init("SHA3-256", "testSalt");
    }

    @Test
    void shouldGenerateSameHashForSameInput() {
        String input = "380671231231";
        String hash1 = HashGenerator.generate(input);
        String hash2 = HashGenerator.generate(input);

        assertEquals(hash1, hash2);
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentInputs() {
        String hash1 = HashGenerator.generate("380671231231");
        String hash2 = HashGenerator.generate("380971231231");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentSalt() {
        String input = "380671231231";
        String hash1 = HashGenerator.generate(input);

        // Перевизначаємо сіль
        HashGenerator.init("SHA3-256", "anotherSalt");
        String hash2 = HashGenerator.generate(input);

        assertNotEquals(hash1, hash2);
    }
}
