package com.demo.app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("username", "password");
    }

    @Test
    void whenSave_thenCanBeFound() {
        userRepository.save(savedUser);
        Long id = savedUser.getId();
        User maybeUser = userRepository.findById(id).orElse(null);
        assertNotNull(maybeUser);
    }

    @Test
    void whenNameExist_thenUserFound() {
        userRepository.save(savedUser);
        String name = savedUser.getName();
        User maybeUser = userRepository.getByName(name).orElse(null);
        assertNotNull(maybeUser);
    }
}
