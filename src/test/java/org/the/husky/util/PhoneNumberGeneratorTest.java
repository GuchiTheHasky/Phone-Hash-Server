package org.the.husky.util;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class PhoneNumberGeneratorTest {


    @Test
    void shouldGenerateCorrectPhoneNumberFormat() {
        PhoneNumberGenerator generator = new PhoneNumberGenerator("38067", 11);

        assertEquals("380670000000", generator.next());
        assertEquals("380670000001", generator.next());
        assertEquals("380670000002", generator.next());
        assertEquals("380670000003", generator.next());
        assertEquals("380670000004", generator.next());
        assertEquals("380670000005", generator.next());
        assertEquals("380670000006", generator.next());
        assertEquals("380670000007", generator.next());
        assertEquals("380670000008", generator.next());
        assertEquals("380670000009", generator.next());
        assertEquals("380670000010", generator.next());
    }

    @Test
    void shouldReturnFalseWhenLimitReached() {
        PhoneNumberGenerator generator = new PhoneNumberGenerator("38067", 2);
        generator.next();
        generator.next();

        assertFalse(generator.hasNext());
    }

}
