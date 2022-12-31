package com.aston.app.dao.impl;

import com.aston.app.dao.UserDAO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;
import com.aston.app.pojo.User;
import com.aston.app.util.DataSourceConfiguration;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class UserDAOImplTest {

    private final static Properties properties;

    /**
     * Перед стартом тестов получаем properties.
     *
     */
    static {
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(Objects.requireNonNull(DataSourceConfiguration.class.getClassLoader().getResourceAsStream("test_configuration.properties"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Container
    private final GenericContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres")).withDatabaseName(properties.getProperty("db.name")).withUsername(properties.getProperty("db.username")).withPassword(properties.getProperty("db.password")).withInitScript(properties.getProperty("db.init_script")).withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    private UserDAO userDAO;

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private User fourthUser;
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