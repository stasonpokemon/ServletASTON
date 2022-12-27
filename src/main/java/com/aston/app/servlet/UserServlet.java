package com.aston.app.servlet;

import com.aston.app.pojo.User;
import com.aston.app.service.UserService;
import com.aston.app.service.impl.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

//@WebServlet(urlPatterns = {"/users"})
public class UserServlet extends HttpServlet {

    private UserService userService;

    private static final Gson GSON = new GsonBuilder().create();


    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURI();
        if ("/".equals(pathInfo) || pathInfo == null) {
            String usersJson = GSON.toJson(userService.findAllUsers());
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(usersJson);
        } else {
            Long userId = Long.parseLong(uri.substring("/users/".length()));
            Optional<User> userById = userService.findUserById(userId);
            if (userById.isPresent()){
                String userJson = GSON.toJson(userById.get());
                resp.setStatus(200);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(userJson);
            }else {
                resp.setStatus(404);
                resp.setHeader("Content-Type", "application/text");
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not found").toString());
            }

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/".equals(pathInfo) || pathInfo == null) {
            System.out.println("post start");
            String userJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
            System.out.println(userJson);
            User user = GSON.fromJson(userJson, User.class);
            userService.saveUser(user);
            resp.setStatus(201);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(GSON.toJson(user));
        } else {
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURI();
        if (!"/".equals(pathInfo) && pathInfo != null) {
            Long userId = Long.parseLong(uri.substring("/users/".length()));
            String userJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
            User user = GSON.fromJson(userJson, User.class);
            userService.updateUser(userId, user);
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(GSON.toJson(user));
        } else {
            resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURI();
        if (!"/".equals(pathInfo) && pathInfo != null) {
            Long userId = Long.parseLong(uri.substring("/users/".length()));
            if (userService.deleteUser(userId)) {
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" was deleted").toString());
                resp.setStatus(200);
            } else {
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not found").toString());
                resp.setStatus(404);
            }
            resp.setHeader("Content-Type", "application/text");
        } else {
            resp.setStatus(400);
        }
    }
}
