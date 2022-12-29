package com.aston.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PassportService {
    void findUsersPassport(HttpServletResponse resp, String s);

    void saveUsersPassport(HttpServletRequest req, HttpServletResponse resp, String s);

    void updateUsersPassport(HttpServletRequest req, HttpServletResponse resp, String s);
}
