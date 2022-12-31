package com.aston.app.dao.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;
import com.aston.app.pojo.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Testcontainers
public class UserDAOImplTest {

    private static final String DB_NAME = "servlet_aston_test";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String DB_INIT_SCRIPT = "postgres_db.sql";


    @Container
    private final GenericContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD)
            .withInitScript(DB_INIT_SCRIPT)
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    private UserDAO userDAO;

    private Passport firstPassport;
    private Passport secondPassport;
    private Passport thirdPassport;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private final List<User> users = new ArrayList<>();

    /**
     * Перед стартом тестов создаем тестувую базу данных и сущности.
     */
    @BeforeEach
    public void init() {
        HikariConfig config = new HikariConfig();
        JdbcDatabaseContainer<?> jdbcDatabaseContainer = (JdbcDatabaseContainer<?>) this.postgresContainer;
        config.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        config.setUsername(jdbcDatabaseContainer.getUsername());
        config.setPassword(jdbcDatabaseContainer.getPassword());
        config.setDriverClassName(jdbcDatabaseContainer.getDriverClassName());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        userDAO = new UserDAOImpl(new HikariDataSource(config));

        firstPassport = new Passport(1L, "Stanislau", "Trebnikau", "Andreevich", "13-07-2001", "Vitebsk");
        secondPassport = new Passport(2L, "test", "test", "test", "13-07-2001", "test");
        thirdPassport = new Passport(3L, "test2", "test2", "test2", "13-07-2001", "test2");

        firstUser = new User(1L, "stasonpokemon", "stasonpokemon@icloud.com", firstPassport);
        secondUser = new User(2L, "test", "test", secondPassport);
        thirdUser = new User(3L, "test2", "test2", thirdPassport);

        users.add(firstUser);
        users.add(secondUser);
        users.add(thirdUser);
    }


    /**
     * Тестируем получение пользователя по идентификатору
     *
     * @throws DBConnectionException
     */
    @Test
    public void findUserByIdTest() throws DBConnectionException {
        assertEquals(Optional.ofNullable(firstUser), userDAO.findUserById(1L));
        assertEquals(Optional.ofNullable(secondUser), userDAO.findUserById(2L));
        assertEquals(Optional.ofNullable(thirdUser), userDAO.findUserById(3L));
    }


    /**
     * Тестируем получение пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException
     */
    @Test
    public void findUserByWrongIdTest() throws DBConnectionException {
        assertEquals(Optional.ofNullable(null), userDAO.findUserById(14L));
    }

    /**
     * Тестируем получение всех пользователей
     *
     * @throws DBConnectionException
     */
    @Test
    public void findAllUsers() throws DBConnectionException {
        assertEquals(users, userDAO.findAllUsers());
    }

    /**
     * Тестируем сохранение нового пользователя
     *
     * @throws DBConnectionException
     */
    @Test
    public void saveNewUser() throws DBConnectionException {
        User newUser = new User();
        newUser.setId(4L);
        newUser.setUsername("new");
        newUser.setEmail("new");
        userDAO.saveUser(newUser);
        assertEquals(4, userDAO.findAllUsers().size());
        assertEquals(Optional.ofNullable(newUser), userDAO.findUserById(4L));
    }

    /**
     * Тестируем сохранение нового пользователя без обязательного поля email. Ожидатеся исключение DBConnectionException
     *
     * @throws DBConnectionException
     */
    @Test()
    public void saveNewUserWithoutRequiredDataField() throws DBConnectionException {
        User newUser = new User();
        newUser.setId(4L);
        newUser.setEmail("new");
        assertThrows(DBConnectionException.class, () -> userDAO.saveUser(newUser));
        assertEquals(3, userDAO.findAllUsers().size());
    }

    /**
     * Тестируем обновление существующего пользователя по идентификатору
     *
     * @throws DBConnectionException
     */
    @Test
    public void updateUserById() throws DBConnectionException {
        User user = userDAO.findUserById(1L).get();
        user.setUsername("change");
        assertTrue(userDAO.updateUser(1L, user));
        assertEquals(Optional.ofNullable(user), userDAO.findUserById(1L));
    }

    /**
     * Тестируем обновление существующего пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException
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
     * @throws DBConnectionException
     */
    @Test
    public void deleteUserById() throws DBConnectionException {
        assertTrue(userDAO.deleteUser(3L));
        assertEquals(2, userDAO.findAllUsers().size());
    }

    /**
     * Тестируем удаление существующего пользователя по несуществующему идентификатору
     *
     * @throws DBConnectionException
     */
    @Test
    public void deleteUserByWrongId() throws DBConnectionException {
        assertFalse(userDAO.deleteUser(13L));
        assertEquals(3, userDAO.findAllUsers().size());
    }
}