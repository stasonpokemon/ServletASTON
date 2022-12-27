package com.aston.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {


    void findUserById(HttpServletResponse resp, String uri);

    void findAllUsers(HttpServletResponse resp);

    void saveUser(HttpServletRequest req, HttpServletResponse resp);

    void updateUser(HttpServletRequest req, HttpServletResponse resp);

    void deleteUser(HttpServletRequest req, HttpServletResponse resp);


}
