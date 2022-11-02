package com.demo.app.auth;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("4c55624ce9f808b9ea6a6484455cac9c26e98a4e251fab62e6eb67e338da7a2c");
    }

    @Test
    void canGetAllClaimsFromToken() {
        String token = jwtUtil.generateToken("sub1");
        String actual = jwtUtil.getAllClaimsFromToken(token).getSubject();
        assertEquals("sub1", actual);
    }

    @Test
    void willThrowException_WhenTokenExpired() {
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJ0ZXN0bmFtZSIsImlhdCI6MTY2NjM1MjQwOCwiZXhwIjoxNjY2OTU3MjA4fQ" +
                ".sLr8d_GLTPW6Y8dt2tiRhgvKRkuD0VTr9bzeURIg8kOIopA1qoMlCD7sledkeGOLduNb4ayH7bxF29-Ng1LrBA";
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.getAllClaimsFromToken(expiredToken));
    }

    @Test
    void canGenerateToken() {
        assertNotNull(jwtUtil.generateToken("name1"));
    }
}