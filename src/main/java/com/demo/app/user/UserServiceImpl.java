package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getByName(String name) {
        Optional<User> user = Optional.ofNullable(userRepository.getByName(name));
        return user.orElseThrow(() -> new RuntimeException("User not found"));
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
    public void saveUser(String name, String password) {
        String hashedPassword = hashPassword(password);
        User user = new User(name, hashedPassword);
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
            String hashedPassword = hashPassword(password);
            BCrypt.Result result = BCrypt.verifyer().verify(hashedPassword.toCharArray(), user.getHashedPassword());
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }
}
