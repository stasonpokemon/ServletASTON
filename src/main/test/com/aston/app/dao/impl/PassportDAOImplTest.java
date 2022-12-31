package com.aston.app.dao.impl;

import com.aston.app.dao.PassportDAO;
import com.aston.app.dao.UserDAO;
import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;
import com.aston.app.util.DataSourceConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class PassportDAOImplTest {

    public static final String BEFORE_SQL =
            "truncate table users restart identity cascade;" +
                    "INSERT INTO users(username, email) " +
                    "VALUES ('stasonpokemon', 'stasonpokemon@icloud.com'); " +
                    "INSERT INTO users(username, email) " +
                    "VALUES ('test', 'test'); " +
                    "INSERT INTO users(username, email) " +
                    "VALUES ('test2', 'test2'); " +
                    "INSERT INTO users(username, email) " +
                    "VALUES ('test3', 'test3'); " +
                    "INSERT INTO passports(name, surname, patronymic, birthday, address, user_id) " +
                    "VALUES ('Stanislau', 'Trebnikau', 'Andreevich', '13-07-2001', 'Vitebsk', 1); " +
                    "INSERT INTO passports(name, surname, patronymic, birthday, address, user_id) " +
                    "VALUES ('test', 'test', 'test', '13-07-2001', 'test', 2); " +
                    "INSERT INTO passports(name, surname, patronymic, birthday, address, user_id) " +
                    "VALUES ('test2', 'test2', 'test2', '13-07-2001', 'test2', 3);";

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
    private static final GenericContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withDatabaseName(properties.getProperty("db.name"))
            .withUsername(properties.getProperty("db.username"))
            .withPassword(properties.getProperty("db.password"))
            .withInitScript(properties.getProperty("db.init_script"))
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    private static HikariConfig config;
    private final HikariDataSource hikariDataSource = new HikariDataSource(config);

    private PassportDAO passportDAO;
    private UserDAO userDAO;

    private Passport testPassport;


    /**
     * Перед стартом тестов создаем тестувую базу данных и сущности.
     */
    @BeforeAll
    public static void init() {
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
        passportDAO = new PassportDAOImpl(hikariDataSource);
        userDAO = new UserDAOImpl(hikariDataSource);

        testPassport = new Passport(1L, "Stanislau", "Trebnikau", "Andreevich", "13-07-2001", "Vitebsk");
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BEFORE_SQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Тестируем сохранение нового для пользователя по его идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    void savePassportByUserId() throws DBConnectionException {
        long userId = 4L;
        Passport newPassport = new Passport(4L, "test3", "test3", "test3", "13-07-2001", "test3");
        assertTrue(passportDAO.saveByUserId(userId, newPassport));
        assertEquals(newPassport, userDAO.findUserById(userId).get().getPassport());
    }

    /**
     * Тестируем сохранение нового паспорта для несуществующего пользователя
     */
    @Test
    void savePassportByWrongUserId() {
        long wrongUserId = 13L;
        Passport newPassport = new Passport(4L, "test3", "test3", "test3", "13-07-2001", "test3");
        assertThrows(DBConnectionException.class, () -> passportDAO.saveByUserId(wrongUserId, newPassport));
    }

    /**
     * Тестируем получение паспорта пользователя по его идентификатору
     *
     * @throws DBConnectionException -
     */
    @Test
    void findPassportByUserId() throws DBConnectionException {
        long userId = 1L;
        assertEquals(Optional.of(testPassport), passportDAO.findPassportByUserId(userId));
    }

    /**
     * Тестируем получение паспорта несуществующего пользователя
     *
     * @throws DBConnectionException -
     */
    @Test
    void findPassportByWrongUserId() throws DBConnectionException {
        long wrongUserId = 13L;
        assertEquals(Optional.empty(), passportDAO.findPassportByUserId(wrongUserId));
    }

    /**
     * Тестируем обновление существующего паспорта по идентификатору пользователя
     *
     * @throws DBConnectionException -
     */
    @Test
    void updateByUserId() throws DBConnectionException {
        long userId = 1L;
        Passport passport = passportDAO.findPassportByUserId(userId).get();
        passport.setName("update");
        assertTrue(passportDAO.updateByUserId(userId, passport));
        assertEquals(passport, passportDAO.findPassportByUserId(userId).get());
    }

    /**
     * Тестируем обновление паспорта без обязательного поля name. Ожидатеся исключение DBConnectionException
     *
     * @throws DBConnectionException -
     */
    @Test
    void updateByUserIdWithWrongDataField() throws DBConnectionException {
        long userId = 1L;
        Passport passport = passportDAO.findPassportByUserId(userId).get();
        passport.setName(null);
        assertThrows(DBConnectionException.class, () -> passportDAO.updateByUserId(userId, passport));
    }

    /**
     * Тестируем обновление паспорта по идентификатору несуществующего пользователя
     *
     * @throws DBConnectionException -
     */
    @Test
    void updateByWrongUserId() throws DBConnectionException {
        long wrongUserId = 13L;
        assertFalse(passportDAO.updateByUserId(wrongUserId, new Passport()));
    }
}