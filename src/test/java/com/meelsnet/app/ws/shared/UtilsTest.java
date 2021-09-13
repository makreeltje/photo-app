package com.meelsnet.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void generateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId);
        assertNotNull(userId2);

        assertTrue(userId.length() == 30);
        assertTrue(!userId.equalsIgnoreCase(userId2));
    }

    @Test
    void hasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("32ovoi390t23");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }

    @Test
    void hasTokenExpired() {
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnSW1VVHlWOGk3NDFoZVpBUnhiSjVCZXd4UjJZMFgiLCJleHAiOjE2MzA0MDE1NjF9.1C1Gih0uH4KrU8guJZaJ4wH_Z0XUrcrB5j4BKSTOBA2PuhD75nWdDcxE8HQrR_5BSmM3dpjUk7HAoE-TfG4sLA";
        boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);

        assertTrue(hasTokenExpired);
    }
}