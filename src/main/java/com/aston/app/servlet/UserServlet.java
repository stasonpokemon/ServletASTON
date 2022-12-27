package com.aston.app.servlet;

import com.aston.app.service.UserService;
import com.aston.app.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet(urlPatterns = {"/users/*"})
public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURI();
        if ("/".equals(pathInfo) || pathInfo == null) {
            userService.findAllUsers(resp);
        } else {
            userService.findUserById(resp, uri);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        userService.saveUser(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        userService.updateUser(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        userService.deleteUser(req, resp);
    }


}
