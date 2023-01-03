package com.aston.app.service.impl;

import com.aston.app.dao.PassportDAO;
import com.aston.app.dao.impl.PassportDAOImpl;
import com.aston.app.pojo.dto.PassportDTO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;
import com.aston.app.service.PassportService;
import com.aston.app.util.HikariCPDataSource;
import com.aston.app.util.HikariCPDataSourceConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

public class PassportServiceImpl implements PassportService {

    private final PassportDAO passportDAO;

    private final ModelMapper modelMapper;

    private static final Gson GSON = new GsonBuilder().create();


    public PassportServiceImpl() {
        HikariConfig config = HikariCPDataSourceConfiguration.getInstance().getConfig();
        this.passportDAO = new PassportDAOImpl(HikariCPDataSource.getInstance(config).getDataSource());
        this.modelMapper = new ModelMapper();
    }

    @Override
    public void findUsersPassport(HttpServletResponse resp, String userIdFromUrl) {
        try {
            long userId = Long.parseLong(userIdFromUrl);
            Optional<Passport> passport = passportDAO.findPassportByUserId(userId);
            if (passport.isPresent()) {
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(GSON.toJson(passportToDTO(passport.get())));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setHeader("Content-Type", "application/text");
                resp.getOutputStream().println(new StringBuilder("Passport by user with id = ").append(userId).append(" not found").toString());
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void saveUsersPassport(HttpServletRequest req, HttpServletResponse resp, String userIdFromUrl) {
        try {
            long userId = Long.parseLong(userIdFromUrl);
            String passportJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
            Passport passport = GSON.fromJson(passportJson, Passport.class);
            passportDAO.saveByUserId(userId, passport);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(GSON.toJson(passportToDTO(passport)));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    public void updateUsersPassport(HttpServletRequest req, HttpServletResponse resp, String userIdFromUrl) {
        try {
            long userId = Long.parseLong(userIdFromUrl);
            String passportJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
            Passport passport = GSON.fromJson(passportJson, Passport.class);
            if (passportDAO.updateByUserId(userId, passport)) {
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(GSON.toJson(passportToDTO(passport)));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setHeader("Content-Type", "application/text");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private PassportDTO passportToDTO(Passport passport) {
        return modelMapper.map(passport, PassportDTO.class);
    }
}
