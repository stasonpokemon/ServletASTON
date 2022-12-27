package com.aston.app.service.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.dao.UserDAOImpl;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.User;
import com.aston.app.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    private static final Gson GSON = new GsonBuilder().create();


    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public void findUserById(HttpServletResponse resp, String uri) {
        try {
            Long userId = Long.parseLong(uri.substring("/users/".length()));
            Optional<User> userById;
            userById = userDAO.findUserById(userId);
            if (userById.isPresent()) {
                String userJson = GSON.toJson(userById.get());
                resp.setStatus(200);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(userJson);
            } else {
                resp.setStatus(404);
                resp.setHeader("Content-Type", "application/text");
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not found").toString());
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(500);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
        }
    }

    @Override
    public void findAllUsers(HttpServletResponse resp) {
        String usersJson;
        try {
            usersJson = GSON.toJson(userDAO.findAllUsers());
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(usersJson);
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(500);
        }
    }

    @Override
    public void saveUser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();
            if ("/".equals(pathInfo) || pathInfo == null) {
                String userJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
                User user = GSON.fromJson(userJson, User.class);
                userDAO.saveUser(user);
                resp.setStatus(201);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(GSON.toJson(user));
            } else {
                resp.setStatus(400);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(500);
        }
    }

    @Override
    public void updateUser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();
            String uri = req.getRequestURI();
            if (!"/".equals(pathInfo) && pathInfo != null) {
                Long userId = Long.parseLong(uri.substring("/users/".length()));
                String userJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
                User user = GSON.fromJson(userJson, User.class);
                if (userDAO.updateUser(userId, user)) {
                    resp.setStatus(200);
                    resp.setHeader("Content-Type", "application/json");
                    resp.getOutputStream().println(GSON.toJson(user));
                } else {
                    resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not updated").toString());
                    resp.setStatus(404);
                }
            } else {
                resp.setStatus(400);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(500);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
        }
    }

    @Override
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();
            String uri = req.getRequestURI();
            if (!"/".equals(pathInfo) && pathInfo != null) {
                Long userId = Long.parseLong(uri.substring("/users/".length()));
                if (userDAO.deleteUser(userId)) {
                    resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" was deleted").toString());
                    resp.setStatus(200);
                } else {
                    resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not deleted").toString());
                    resp.setStatus(404);
                }
                resp.setHeader("Content-Type", "application/text");
            } else {
                resp.setStatus(400);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(500);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
        }
    }
}
