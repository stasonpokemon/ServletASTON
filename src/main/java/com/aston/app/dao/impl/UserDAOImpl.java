package com.aston.app.dao.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.pojo.Passport;
import com.aston.app.pojo.User;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final String SAVE_USER_SQl = "INSERT INTO users (username, email) VALUES(?, ?);";
    private static final String FIND_USER_BY_ID_SQl = "SELECT U.id as u_id, U.username as u_username, U.email as u_email, P.id as p_id, P.name as p_name, P.surname as p_surname, P.patronymic as p_patronymic, P.birthday as p_birthday, P.address as p_address FROM users AS U LEFT JOIN passports AS P ON U.id = P.user_id WHERE U.id = ?;";
    private static final String FIND_ALL_USERS_SQl = "SELECT U.id as u_id, U.username as u_username, U.email as u_email, P.id as p_id, P.name as p_name, P.surname as p_surname, P.patronymic as p_patronymic, P.birthday as p_birthday, P.address as p_address FROM users AS U LEFT JOIN passports AS P ON U.id = P.user_id;";
    private static final String UPDATE_USER_BY_ID_SQl = "UPDATE users SET username = ?, email = ? WHERE id = ?;";
    private static final String DELETE_USER_BY_ID_SQl = "DELETE FROM users WHERE id = ?;";

    @Override
    public void saveUser(User user) throws DBConnectionException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER_SQl)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
    }

    @Override
    public Optional<User> findUserById(Long userId) throws DBConnectionException {
        User user = null;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQl)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User();
                user.setId(userId);
                user.setUsername(resultSet.getString("u_username"));
                user.setEmail(resultSet.getString("u_email"));
                long passportId = resultSet.getLong("p_id");
                Passport passport;
                if (passportId != 0) {
                    passport = new Passport();
                    passport.setId(passportId);
                    passport.setName(resultSet.getString("p_name"));
                    passport.setSurname(resultSet.getString("p_surname"));
                    passport.setPatronymic(resultSet.getString("p_patronymic"));
                    passport.setBirthday(resultSet.getString("p_birthday"));
                    passport.setAddress(resultSet.getString("p_address"));
                    user.setPassport(passport);
                }
            }
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAllUsers() throws DBConnectionException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS_SQl)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("u_id"));
                user.setUsername(resultSet.getString("u_username"));
                user.setEmail(resultSet.getString("u_email"));
                long passportId = resultSet.getLong("p_id");
                Passport passport;
                if (passportId != 0) {
                    passport = new Passport();
                    passport.setId(passportId);
                    passport.setName(resultSet.getString("p_name"));
                    passport.setSurname(resultSet.getString("p_surname"));
                    passport.setPatronymic(resultSet.getString("p_patronymic"));
                    passport.setBirthday(resultSet.getString("p_birthday"));
                    passport.setAddress(resultSet.getString("p_address"));
                    user.setPassport(passport);
                }
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
        return users;
    }

    @Override
    public boolean updateUser(Long userId, User user) throws DBConnectionException {
        boolean isUpdated;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_BY_ID_SQl)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setLong(3, userId);
            isUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }

        return isUpdated;
    }

    @Override
    public boolean deleteUser(Long userId) throws DBConnectionException {
        boolean isDeleted;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_ID_SQl)) {
            preparedStatement.setLong(1, userId);
            isDeleted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
        return isDeleted;

    }
}
