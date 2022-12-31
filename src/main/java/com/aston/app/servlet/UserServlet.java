package com.aston.app.servlet;

import com.aston.app.service.PassportService;
import com.aston.app.service.UserService;
import com.aston.app.service.impl.PassportServiceImpl;
import com.aston.app.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserServlet extends HttpServlet {

    private UserService userService;

    private PassportService passportService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
        passportService = new PassportServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String[] splitPathInfo;
        if (pathInfo != null) {
            splitPathInfo = pathInfo.split("/");
            if (splitPathInfo.length == 3 && "passport".equals(splitPathInfo[2])) {
                String userIdFromUrl = splitPathInfo[1];
                passportService.findUsersPassport(resp, userIdFromUrl);
            } else if ("/".equals(pathInfo)) {
                userService.findAllUsers(resp);
            } else if (splitPathInfo.length == 2) {
                userService.findUserById(resp, splitPathInfo[1]);
            }
        } else {
            userService.findAllUsers(resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String[] splitPathInfo;
        if (pathInfo != null) {
            splitPathInfo = pathInfo.split("/");
            if (splitPathInfo.length == 3 && "passport".equals(splitPathInfo[2])) {
                passportService.saveUsersPassport(req, resp, splitPathInfo[1]);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            userService.saveUser(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String[] splitPathInfo;
        if (pathInfo != null) {
            splitPathInfo = pathInfo.split("/");
            if (splitPathInfo.length == 3 && "passport".equals(splitPathInfo[2])) {
                passportService.updateUsersPassport(req, resp, splitPathInfo[1]);
            } else if (splitPathInfo.length == 2) {
                userService.updateUser(req, resp, splitPathInfo[1]);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String[] splitPathInfo;
        if (pathInfo != null) {
            splitPathInfo = pathInfo.split("/");
            if (splitPathInfo.length == 2) {
                userService.deleteUser(req, resp, splitPathInfo[1]);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
