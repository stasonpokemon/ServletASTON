package com.aston.app.util;

import com.zaxxer.hikari.HikariConfig;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public class DataSourceConfiguration {


    private static DataSourceConfiguration instance;

    private HikariConfig config = new HikariConfig();

    private Properties properties;

    private DataSourceConfiguration() throws IOException {
        properties = new Properties();
        properties.load(new InputStreamReader(Objects.requireNonNull(DataSourceConfiguration.class.getClassLoader().getResourceAsStream("configuration.properties"))));
        config.setDriverClassName(properties.getProperty("datasource.driver-class-name"));
        config.setJdbcUrl(properties.getProperty("datasource.url"));
        config.setUsername(properties.getProperty("datasource.username"));
        config.setPassword(properties.getProperty("datasource.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    @SneakyThrows
    public static DataSourceConfiguration getInstance() {
        if (instance == null) {
            instance = new DataSourceConfiguration();
        }
        return instance;
    }

    public HikariConfig getConfig() {
        return config;
    }

    //    static {
//        config.setDriverClassName(org.postgresql.Driver.class.getName());
//        config.setJdbcUrl("jdbc:postgresql://localhost:5432/servlet_aston");
//        config.setUsername("postgres");
//        config.setPassword("postgres");
//        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//    }

//    public static Connection getConnection() {
//        Properties properties = new Properties();
//        try {
//            properties.load(new InputStreamReader(Objects.requireNonNull(DataSource.class.getClassLoader().getResourceAsStream("configuration.properties"))));
//            Class.forName(properties.getProperty("datasource.driver-class-name"));
//            return DriverManager.getConnection(
//                    properties.getProperty("datasource.url"),
//                    properties.getProperty("datasource.url"),
//                    properties.getProperty("datasource.password"));
//        } catch (IOException | ClassNotFoundException | SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
