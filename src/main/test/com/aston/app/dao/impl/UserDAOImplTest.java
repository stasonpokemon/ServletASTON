package com.aston.app.dao.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;
import com.aston.app.pojo.User;
import com.aston.app.util.HikariCPDataSource;
import com.aston.app.util.HikariCPDataSourceConfiguration;
import com.aston.app.util.PostgreSQLContainerUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static com.aston.app.util.PostgreSQLContainerUtil.BEFORE_SQL;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class UserDAOImplTest {



    @Container
    private static final GenericContainer<?> postgresContainer = PostgreSQLContainerUtil.getInstance();
    private static HikariConfig config;
    private final HikariDataSource hikariDataSource = new HikariDataSource(config);

    private UserDAO userDAO;

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private User fourthUser;
    private final List<User> users = new ArrayList<>();


    /**
     * Перед стартом тестов создаем тестувую базу данных и сущности.
     */
    @BeforeAll
    public static void initBeforeAll() {
        config = new HikariConfig();
        JdbcDatabaseContainer<?> jdbcDatabaseContainer = (JdbcDatabaseContainer<?>) postgresContainer;
        config.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        config.setUsername(jdbcDatabaseContainer.getUsername());
        config.setPassword(jdbcDatabaseContainer.getPassword());
        config.setDriverClassName(jdbcDatabaseContainer.getDriverClassName());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");


    }

    @BeforeEach
    public void initBeforeEach() {
        userDAO = new UserDAOImpl(hikariDataSource);

        Passport firstPassport = new Passport(1L, "Stanislau", "Trebnikau", "Andreevich", "13-07-2001", "Vitebsk");
        Passport secondPassport = new Passport(2L, "test", "test", "test", "13-07-2001", "test");
        Passport thirdPassport = new Passport(3L, "test2", "test2", "test2", "13-07-2001", "test2");

        firstUser = new User(1L, "stasonpokemon", "stasonpokemon@icloud.com", firstPassport);
        secondUser = new User(2L, "test", "test", secondPassport);
        thirdUser = new User(3L, "test2", "test2", thirdPassport);
        fourthUser = new User(4L, "test3", "test3", null);

        users.add(firstUser);
        users.add(secondUser);
        users.add(thirdUser);
        users.add(fourthUser);
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BEFORE_SQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Тестируем получение пользователя по идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void findUserByIdTest() throws DBConnectionException {
        assertEquals(Optional.ofNullable(firstUser), userDAO.findUserById(1L));
        assertEquals(Optional.ofNullable(secondUser), userDAO.findUserById(2L));
        assertEquals(Optional.ofNullable(thirdUser), userDAO.findUserById(3L));
        assertEquals(Optional.ofNullable(fourthUser), userDAO.findUserById(4L));
    }


    /**
     * Тестируем получение пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void findUserByWrongIdTest() throws DBConnectionException {
        assertEquals(Optional.empty(), userDAO.findUserById(14L));
    }

    /**
     * Тестируем получение всех пользователей
     *
     * @throws DBConnectionException -
     */
    @Test
    public void findAllUsers() throws DBConnectionException {
        assertEquals(users, userDAO.findAllUsers());
    }

    /**
     * Тестируем сохранение нового пользователя
     *
     * @throws DBConnectionException -
     */
    @Test
    public void saveNewUser() throws DBConnectionException {
        User newUser = new User();
        newUser.setId(5L);
        newUser.setUsername("new");
        newUser.setEmail("new");
        assertTrue(userDAO.saveUser(newUser));
        assertEquals(5, userDAO.findAllUsers().size());
        assertEquals(Optional.of(newUser), userDAO.findUserById(5L));
    }

    /**
     * Тестируем сохранение нового пользователя без обязательного поля email. Ожидатеся исключение DBConnectionException
     *
     * @throws DBConnectionException -
     */
    @Test()
    public void saveNewUserWithoutRequiredDataField() throws DBConnectionException {
        User newUser = new User();
        newUser.setId(4L);
        newUser.setEmail("new");
        assertThrows(DBConnectionException.class, () -> userDAO.saveUser(newUser));
        assertEquals(4, userDAO.findAllUsers().size());
    }

    /**
     * Тестируем обновление существующего пользователя по идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void updateUserById() throws DBConnectionException {
        User user = userDAO.findUserById(1L).get();
        user.setUsername("change");
        assertTrue(userDAO.updateUser(1L, user));
        assertEquals(Optional.of(user), userDAO.findUserById(1L));
    }

    /**
     * Тестируем обновление существующего пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void updateUserByWrongId() throws DBConnectionException {
        User user = userDAO.findUserById(1L).get();
        user.setUsername("change");
        assertFalse(userDAO.updateUser(13L, user));
    }

    /**
     * Тестируем удаление существующего пользователя по идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void deleteUserById() throws DBConnectionException {
        assertTrue(userDAO.deleteUser(3L));
        assertEquals(3, userDAO.findAllUsers().size());
    }

    /**
     * Тестируем удаление существующего пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    public void deleteUserByWrongId() throws DBConnectionException {
        assertFalse(userDAO.deleteUser(13L));
        assertEquals(4, userDAO.findAllUsers().size());
    }
}