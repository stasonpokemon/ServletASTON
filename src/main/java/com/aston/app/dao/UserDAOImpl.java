package com.aston.app.dao;

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

    private static final String SAVE_USER_SQl = "INSERT INTO users (name, email) VALUES(?, ?);";
    private static final String FIND_USER_BY_ID_SQl = "SELECT * FROM users WHERE id = ?;";
    private static final String FIND_ALL_USERS_SQl = "SELECT * FROM users;";
    private static final String UPDATE_USER_BY_ID_SQl = "UPDATE users SET name = ?, email = ? WHERE id = ?;";
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
                user.setUsername(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
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
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
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
            preparedStatement.setLong(4, userId);
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
