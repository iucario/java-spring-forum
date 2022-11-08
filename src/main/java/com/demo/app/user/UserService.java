package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.exception.AppException;
import com.demo.app.exception.AppException.UserNotFoundException;
import com.demo.app.favorite.FavUserPost;
import com.demo.app.favorite.FavUserPostRepository;
import com.demo.app.post.Post;
import com.demo.app.user.userStats.UserStats;
import com.demo.app.user.userStats.UserStatsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    private final FavUserPostRepository favUserPostRepository;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, UserStatsRepository userStatsRepository,
                       FavUserPostRepository favUserPostRepository, AuthService authService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
        this.favUserPostRepository = favUserPostRepository;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    public List<Post> getUserFavorites(Long userId) {
        return favUserPostRepository.findByUser(userId)
                .stream()
                .map(FavUserPost::getPost)
                .toList();
    }

    public UserStats getUserStats(Long userId) {
        return userStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException.UserNotFoundException(userId));
    }

    public UserDto saveUserStats(UserStats userStats) {
        UserStats saved = userStatsRepository.save(userStats);
        return new UserDto(saved.getUser(), saved);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getByName(String name) {
        return userRepository.findByName(name.toLowerCase()).orElseThrow(() ->
                new AppException.UserNotFoundException(name));
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));
    }

    public UserDto getUserProfile(Long id) {
        User user = getById(id);
        UserStats userStats = getUserStats(id);
        return new UserDto(user, userStats);
    }

    @Transactional
    public UserDto createUser(UserDto.UserCreate userCreate) {
        if (userRepository.findByName(userCreate.name).isPresent()) {
            throw new AppException.UserExistsException(userCreate.name);
        }
        String hashedPassword = hashPassword(userCreate.password);
        User user = new User(userCreate.name, hashedPassword);
        User saved = userRepository.save(user);
        UserStats userStats = userStatsRepository.save(new UserStats(saved));
        return new UserDto(saved, userStats);
    }

    public UserDto getUserInfo(User user) {
        UserStats userStats = getUserStats(user.getId());
        return new UserDto(user, userStats);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto.LoginResponse login(UserDto.UserLogin login) {
        if (!authService.authenticate(login.name, login.password)) {
            throw new AppException.UnauthorizedException();
        }
        return new UserDto.LoginResponse(jwtUtil.generateToken(login.name));
    }

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }
}
