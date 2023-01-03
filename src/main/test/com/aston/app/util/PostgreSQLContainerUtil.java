package com.aston.app.util;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

public class PostgreSQLContainerUtil {

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

    private static GenericContainer<?> postgresContainer;

    private final static Properties properties;

    /**
     * Перед стартом тестов получаем properties.
     *
     */
    static {
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(Objects.requireNonNull(HikariCPDataSourceConfiguration.class.getClassLoader().getResourceAsStream("test_configuration.properties"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GenericContainer<?> getInstance() {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
                    .withDatabaseName(properties.getProperty("db.name"))
                    .withUsername(properties.getProperty("db.username"))
                    .withPassword(properties.getProperty("db.password"))
                    .withInitScript(properties.getProperty("db.init_script"))
                    .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));
        }
        return postgresContainer;
    }
}




