package com.aston.app.service.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.dao.impl.UserDAOImpl;
import com.aston.app.pojo.dto.PassportDTO;
import com.aston.app.pojo.dto.UserDTO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.User;
import com.aston.app.service.UserService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    private static final Gson GSON = new GsonBuilder().create();

    private final ModelMapper modelMapper;

    public UserServiceImpl() {
        HikariConfig config = HikariCPDataSourceConfiguration.getInstance().getConfig();
        this.userDAO = new UserDAOImpl(HikariCPDataSource.getInstance(config).getDataSource());
        this.modelMapper = new ModelMapper();
    }

    @Override
    public void findUserById(HttpServletResponse resp, String userIdFromUrl) {
        try {
            Long userId = Long.parseLong(userIdFromUrl);
            Optional<User> userById = userDAO.findUserById(userId);
            if (userById.isPresent()) {
                String userJson = GSON.toJson(userToDTO(userById.get()));
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(userJson);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setHeader("Content-Type", "application/text");
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not found").toString());
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void findAllUsers(HttpServletResponse resp) {
        try {
            List<UserDTO> usersDTO = userDAO.findAllUsers().stream().map(this::userToDTO).collect(Collectors.toList());
            String usersJson = GSON.toJson(usersDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().println(usersJson);
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(GSON.toJson(userToDTO(user)));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(HttpServletRequest req, HttpServletResponse resp, String userIdFromUrl) {
        try {
            Long userId = Long.parseLong(userIdFromUrl);
            String userJson = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));
            User user = GSON.fromJson(userJson, User.class);
            if (userDAO.updateUser(userId, user)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Content-Type", "application/json");
                resp.getOutputStream().println(GSON.toJson(userToDTO(user)));
            } else {
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not updated").toString());
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp, String userIdFromUrl) {
        try {
            Long userId = Long.parseLong(userIdFromUrl);
            if (userDAO.deleteUser(userId)) {
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" was deleted").toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.getOutputStream().println(new StringBuilder("User with id = ").append(userId).append(" not deleted").toString());
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            resp.setHeader("Content-Type", "application/text");
        } catch (DBConnectionException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private UserDTO userToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if (user.getPassport() != null) {
            PassportDTO passportDTO = modelMapper.map(user.getPassport(), PassportDTO.class);
            userDTO.setPassport(passportDTO);
        }
        return userDTO;
    }
}
