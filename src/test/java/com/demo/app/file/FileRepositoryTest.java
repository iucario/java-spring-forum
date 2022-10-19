package com.demo.app.file;

import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class FileRepositoryTest {
    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        userRepository.save(savedUser);
        FileEntity savedFile = new FileEntity("filename", "url", savedUser);
        fileRepository.save(savedFile);
    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByName() {
        FileEntity maybeFile = fileRepository.findByName("filename").orElse(null);
        assertNotNull(maybeFile);
    }

    @Test
    void findAllByUser() {
        FileEntity maybeFile = fileRepository.findAllByUser(savedUser.getId()).get(0);
        assertNotNull(maybeFile);
    }
}