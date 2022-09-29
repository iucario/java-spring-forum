package com.demo.app.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.repository.User;
import com.demo.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getByName(String name) {
        User user = null;
        user = userRepository.getByName(name);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @Override
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

    @Override
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public Boolean authenticate(String name, String password) {
        try {
            User user = userRepository.getByName(name);
            char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(6, password.toCharArray());
            String hashedPassword = new String(bcryptChars);
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getHashedPassword());
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }
}
