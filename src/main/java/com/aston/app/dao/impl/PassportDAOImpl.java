package com.aston.app.dao.impl;

import com.aston.app.dao.PassportDAO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PassportDAOImpl implements PassportDAO {

    private static final String SAVE_PASSPORT_SQl = "INSERT INTO passports(name, surname, patronymic, birthday, address, user_id) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String FIND_PASSPORT_BY_USER_ID_SQl = "SELECT P.id as p_id, P.name as p_name, P.surname as p_surname, P.patronymic as p_patronymic, P.birthday as p_birthday, P.address as p_address FROM users AS U RIGHT JOIN passports AS P ON U.id = P.user_id WHERE U.id = ?;";
    private static final String UPDATE_PASSPORT_BY_USER_ID_SQl = "UPDATE passports SET name = ?, surname = ?, patronymic = ?, birthday = ?, address = ? Where id = (SELECT p.id from passports p join users u on u.id = p.user_id where u.id = ?);";

    private final DataSource dataSource;

    public PassportDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveByUserId(Long userId, Passport passport) throws DBConnectionException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PASSPORT_SQl)) {
            preparedStatement.setString(1, passport.getName());
            preparedStatement.setString(2, passport.getSurname());
            preparedStatement.setString(3, passport.getPatronymic());
            preparedStatement.setString(4, passport.getBirthday());
            preparedStatement.setString(5, passport.getAddress());
            preparedStatement.setLong(6, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
    }

    @Override
    public Optional<Passport> findPassportByUserId(Long userId) throws DBConnectionException {
        Passport passport = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(FIND_PASSPORT_BY_USER_ID_SQl)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                passport = new Passport();
                passport.setId(resultSet.getLong("p_id"));
                passport.setName(resultSet.getString("p_name"));
                passport.setSurname(resultSet.getString("p_surname"));
                passport.setPatronymic(resultSet.getString("p_patronymic"));
                passport.setBirthday(resultSet.getString("p_birthday"));
                passport.setAddress(resultSet.getString("p_address"));
            }
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
        return Optional.ofNullable(passport);
    }

    @Override
    public boolean updateByUserId(Long userId, Passport passport) throws DBConnectionException {
        boolean isUpdated;
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PASSPORT_BY_USER_ID_SQl)) {
            preparedStatement.setString(1, passport.getName());
            preparedStatement.setString(2, passport.getSurname());
            preparedStatement.setString(3, passport.getPatronymic());
            preparedStatement.setString(4, passport.getBirthday());
            preparedStatement.setString(5, passport.getAddress());
            preparedStatement.setLong(6, userId);
            isUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DBConnectionException("Connection problems");
        }
        return isUpdated;
    }
}
