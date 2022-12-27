package com.aston.app.service.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.dao.UserDAOImpl;
import com.aston.app.pojo.User;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;


    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }


    @Override
    public void saveUser(User user) {
        try {
            userDAO.saveUser(user);
        } catch (DBConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        try {
            return userDAO.findUserById(userId);
        } catch (DBConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAllUsers() {
        try {
            return userDAO.findAllUsers();
        } catch (DBConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateUser(Long userId, User user) {
        try {
            return userDAO.updateUser(userId, user);
        } catch (DBConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        try {
            return userDAO.deleteUser(userId);
        } catch (DBConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}
