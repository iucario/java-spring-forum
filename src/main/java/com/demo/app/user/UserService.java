package com.demo.app.user;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getByName(String name);

    User getById(Long id);

    void saveUser(String name, String password);

    void deleteUserById(Long id);

    Boolean authenticate(String name, String password);
}
