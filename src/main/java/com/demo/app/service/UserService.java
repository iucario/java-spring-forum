package com.demo.app.service;

import com.demo.app.repository.User;

import java.util.List;


public interface UserService {
    List<User> getAll();

    User getByName(String name);

    User getById(Long id);

    void saveUser(User user);

    void deleteUserById(Long id);

    Boolean authenticate(String name, String password);
}
