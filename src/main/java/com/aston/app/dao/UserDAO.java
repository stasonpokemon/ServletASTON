package com.aston.app.dao;

import com.aston.app.pojo.User;
import com.aston.app.exception.DBConnectionException;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    void saveUser(User user) throws DBConnectionException;

    Optional<User> findUserById(Long userId) throws DBConnectionException;

    List<User> findAllUsers() throws DBConnectionException;

    boolean updateUser(Long userId, User user) throws DBConnectionException;

    boolean deleteUser(Long userId) throws DBConnectionException;


}
