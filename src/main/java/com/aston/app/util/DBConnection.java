package com.aston.app.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(Objects.requireNonNull(DBConnection.class.getClassLoader().getResourceAsStream("configuration.properties"))));
            Class.forName(properties.getProperty("datasource.driver-class-name"));
            return DriverManager.getConnection(
                    properties.getProperty("datasource.url"),
                    properties.getProperty("datasource.username"),
                    properties.getProperty("datasource.password"));
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
