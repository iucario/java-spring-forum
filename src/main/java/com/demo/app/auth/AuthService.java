package com.demo.app.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.exception.AppException;
import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findByName(claims.get("sub", String.class)).orElseThrow(
                () -> new AppException.UnauthorizedException("Invalid token"));
    }
}
