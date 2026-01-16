package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskbuddyEntityTest {

    @Test
    void constructor_setsCreatedAt_andInitialValues() {
        long before = System.currentTimeMillis();

        Taskbuddy t = new Taskbuddy("Hello", false);

        long after = System.currentTimeMillis();

        assertEquals("Hello", t.getTitle());
        assertFalse(t.isDone());
        assertNotNull(t.getCreatedAt());
        assertTrue(t.getCreatedAt() >= before && t.getCreatedAt() <= after);
    }
}
