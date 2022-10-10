package com.demo.app.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.demo.app.item.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private static String hashPassword(String password) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2B).hashToChar(12, password.toCharArray());
        return new String(bcryptChars);
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User getByName(String name) {
        Optional<User> user = userRepository.getByName(name);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
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
        if (userRepository.getByName(name).isPresent()) {
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
        User user = userRepository.getByName(name).orElse(null);
        if (user == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getHashedPassword());
        return result.verified;
    }

    public int countItems(Long id) {
        return itemRepository.countAll(id);
    }
}
