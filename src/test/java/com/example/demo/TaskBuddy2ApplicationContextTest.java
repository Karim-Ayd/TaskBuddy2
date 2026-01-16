package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TaskBuddy2ApplicationContextTest {

    @Test
    void contextLoads() {
        // startet Spring Context mit H2 (test profile)
    }
}
