package com.aston.app.service;

import com.aston.app.pojo.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user);

    Optional<User> findUserById(Long userId);

    List<User> findAllUsers();

    boolean updateUser(Long userId, User user);

    boolean deleteUser(Long userId);


}
