package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.post.PostRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User findByName(String name) {
        Optional<User> user = userRepository.findByName(name.toLowerCase());
        return user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found for name :: " + name));
    }

    public User getById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        User user = null;
        if (optional.isPresent()) {
            user = optional.get();
        } else {
            throw new RuntimeException(" User not found for id :: " + id);
        }
        return user;
    }

    public void saveUser(String name, String password) {
        if (userRepository.findByName(name).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        String hashedPassword = hashPassword(password);
        User user = new User(name, hashedPassword);
        this.userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    public Boolean authenticate(String name, String password) {
        User user = userRepository.findByName(name).orElse(null);
        if (user == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getHashedPassword());
        return result.verified;
    }

    public int countItems(Long id) {
        return postRepository.countAll(id);
    }

    public User getUser(HttpServletRequest request) {
        final Claims claims = (Claims) request.getAttribute("claims");
        try {
            return findByName(claims.get("sub", String.class));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }
}
