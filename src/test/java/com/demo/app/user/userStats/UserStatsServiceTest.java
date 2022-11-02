package com.demo.app.user.userStats;

import com.demo.app.exception.AppException;
import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserStatsServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserStatsRepository userStatsRepository;
    private User savedUser;
    private UserStats savedUserStats;
    private UserStatsService userStatsService;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        userRepository.save(savedUser);
        savedUserStats = new UserStats(savedUser);
        userStatsRepository.save(savedUserStats);
        userStatsService = new UserStatsService(userStatsRepository);
    }

    @Test
    void canGetUserStats() {
        when(userStatsRepository.findByUserId(any())).thenReturn(Optional.of(savedUserStats));
        UserStats maybeUserStats = userStatsRepository.findByUserId(savedUser.getId()).orElse(null);
        verify(userStatsRepository).findByUserId(any());
        assertNotNull(maybeUserStats);
    }

    @Test
    void willThrowException_WhenUserNotFound() {
        when(userStatsRepository.findByUserId(any())).thenReturn(Optional.empty());
        assertThrows(AppException.NotFoundException.class, () -> userStatsService.getUserStats(savedUser.getId()));
        verify(userStatsRepository).findByUserId(any());
    }
}
