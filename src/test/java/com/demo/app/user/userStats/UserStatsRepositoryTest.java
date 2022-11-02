package com.demo.app.user.userStats;

import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserStatsRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserStatsRepository userStatsRepository;
    private User savedUser;
    private UserStats savedUserStats;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        userRepository.save(savedUser);
        savedUserStats = new UserStats(savedUser);
        userStatsRepository.save(savedUserStats);
    }

    @AfterEach
    void tearDown() {
        userStatsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void canFindByUserId() {
        UserStats maybeUserStats = userStatsRepository.findByUserId(savedUser.getId()).orElse(null);
        assertNotNull(maybeUserStats);
    }
}