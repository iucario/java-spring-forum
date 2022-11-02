package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.exception.AppException;
import com.demo.app.user.userStats.UserStats;
import com.demo.app.user.userStats.UserStatsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserStatsService userStatsService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, UserStatsService userStatsService, AuthService authService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userStatsService = userStatsService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getByName(String name) {
        return userRepository.findByName(name.toLowerCase()).orElseThrow(() ->
                new AppException.NotFoundException(String.format("User not found for name: %s", name)));
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new AppException.NotFoundException(String.format("User not found for id: %d", id)));
    }

    public UserDto getUserProfile(Long id) {
        User user = getById(id);
        UserStats userStats = userStatsService.getUserStats(id);
        return new UserDto(user, userStats);
    }

    @Transactional
    public UserDto createUser(UserDto.UserCreate userCreate) {
        if (userRepository.findByName(userCreate.name).isPresent()) {
            throw new AppException.UserExistsException(String.format("User already exists: %s", userCreate.name));
        }
        String hashedPassword = hashPassword(userCreate.password);
        User user = new User(userCreate.name, hashedPassword);
        User saved = userRepository.save(user);
        UserStats userStats = userStatsService.save(new UserStats(saved));
        return new UserDto(saved, userStats);
    }

    public UserDto getUserInfo(User user) {
        UserStats userStats = userStatsService.getUserStats(user.getId());
        return new UserDto(user, userStats);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto.LoginResponse login(UserDto.UserLogin login) {
        if (!authService.authenticate(login.name, login.password)) {
            throw new AppException.UnauthorizedException("Invalid username or password");
        }
        return new UserDto.LoginResponse(jwtUtil.generateToken(login.name));
    }

}
