package com.demo.app.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilTest {
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("signKey012345678901234567890123456789");
    }

    @Test
    void getAllClaimsFromToken() {
        String token = jwtUtil.generateToken("sub1");
        String actual = jwtUtil.getAllClaimsFromToken(token).getSubject();
        assertEquals("sub1", actual);
    }

    @Test
    void generateToken() {
        assertNotNull(jwtUtil.generateToken("name1"));
    }
}