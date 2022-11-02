package com.demo.app.user.userStats;

import com.demo.app.exception.AppException;
import org.springframework.stereotype.Service;

@Service
public class UserStatsService {
    private final UserStatsRepository userStatsRepository;

    public UserStatsService(UserStatsRepository userStatsRepository) {
        this.userStatsRepository = userStatsRepository;
    }

    public UserStats getUserStats(Long userId) {
        return userStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException.NotFoundException("UserStats not found for user with id: " + userId));
    }
}
