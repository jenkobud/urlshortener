package com.roadmapsh.urlshortener.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    @Test
    void generate_shouldReturnCodeOfLength6() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code = generator.generate();
        assertEquals(6, code.length());
    }

    @Test
    void generate_shouldReturnOnlyAlphanumericCharacters() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code = generator.generate();
        assertTrue(code.matches("^[A-Za-z0-9]+$"), "Code should only contain alphanumeric characters");
    }

    @RepeatedTest(10)
    void generate_shouldReturnDifferentCodesOnMultipleCalls() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code1 = generator.generate();
        String code2 = generator.generate();
        // Note: There's a tiny chance of collision, but statistically improbable
        assertNotEquals(code1, code2, "Generated codes should be different");
    }
}
