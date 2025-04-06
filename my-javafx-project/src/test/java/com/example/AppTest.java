package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void testAddition() {
        int result = 2 + 2;
        assertEquals(4, result, "2 + 2 sollte 4 ergeben");
    }
}
