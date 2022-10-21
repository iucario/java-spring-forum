package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.exception.AppException;
import com.demo.app.post.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PostRepository postRepository, AuthService authService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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
        int totalPosts = postRepository.countUserPosts(id);
        return new UserDto(user, totalPosts);
    }

    public UserDto register(UserDto.UserCreate userCreate) {
        if (userRepository.findByName(userCreate.name).isPresent()) {
            throw new AppException.UserExistsException(String.format("User already exists: %s", userCreate.name));
        }
        String hashedPassword = hashPassword(userCreate.password);
        User user = new User(userCreate.name, hashedPassword);
        return new UserDto(userRepository.save(user), 0);
    }

    public UserDto getUserInfo(User user) {
        int totalPosts = postRepository.countUserPosts(user.getId());
        return new UserDto(user, totalPosts);
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
