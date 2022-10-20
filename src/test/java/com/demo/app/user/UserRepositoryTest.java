package com.demo.app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        userRepository.save(savedUser);
    }

    @Test
    void whenSave_thenCanBeFound() {
        Long id = savedUser.getId();
        User maybeUser = userRepository.findById(id).orElse(null);
        assertNotNull(maybeUser);
    }

    @Test
    void whenNameExist_thenUserFound() {
        String name = savedUser.getName();
        User maybeUser = userRepository.findByName(name).orElse(null);
        assertNotNull(maybeUser);
    }

    @Test
    void whenNameNotExist_thenUserNotFound() {
        String name = "notexist";
        User maybeUser = userRepository.findByName(name).orElse(null);
        assertNull(maybeUser);
    }

    @Test
    void canFindByName() {
        String name = "TESTNAME";
        User maybeUser = userRepository.findByName(name).orElse(null);
        assertNotNull(maybeUser);
    }
}
