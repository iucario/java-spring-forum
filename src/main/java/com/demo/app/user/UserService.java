package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.post.PostRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getByName(String name) {
        Optional<User> user = userRepository.findByName(name.toLowerCase());
        return user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found for name :: " + name));
    }

    public User getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found for id :: " + id));
    }

    public UserDto getUserProfile(Long id) {
        User user = getById(id);
        int totalPosts = postRepository.countUserPosts(id);
        return new UserDto(user, totalPosts);
    }

    public void saveUser(String name, String password) {
        if (userRepository.findByName(name).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        String hashedPassword = hashPassword(password);
        User user = new User(name, hashedPassword);
        userRepository.save(user);
    }

    public ResponseEntity<String> register(UserDto.UserCreate user) {
        if (isUserNameExists(user.name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        saveUser(user.name, user.password);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created");
    }

    public UserDto getUserInfo(HttpServletRequest request) {
        final Claims claims = (Claims) request.getAttribute("claims");
        final User user = getByName(claims.get("sub", String.class));
        int totalPosts = postRepository.countUserPosts(user.getId());
        return new UserDto(user, totalPosts);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Boolean authenticate(String name, String password) {
        User user = userRepository.findByName(name).orElse(null);
        if (user == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getHashedPassword());
        return result.verified;
    }

    public User getUser(HttpServletRequest request) {
        final Claims claims = (Claims) request.getAttribute("claims");
        try {
            return getByName(claims.get("sub", String.class));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    private Boolean isUserNameExists(String name) {
        return userRepository.findByName(name).isPresent();
    }
}
