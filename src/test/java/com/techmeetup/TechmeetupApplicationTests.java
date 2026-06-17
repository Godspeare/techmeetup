package com.techmeetup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TechmeetupApplicationTests {

    @Test
    void applicationClassIsAnnotated() {
        assertNotNull(TechmeetupApplication.class.getAnnotation(SpringBootApplication.class));
    }
}
