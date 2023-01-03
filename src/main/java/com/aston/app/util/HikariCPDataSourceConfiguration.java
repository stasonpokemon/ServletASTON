package com.aston.app.util;

import com.zaxxer.hikari.HikariConfig;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public class HikariCPDataSourceConfiguration {


    private static HikariCPDataSourceConfiguration instance;

    private final HikariConfig config = new HikariConfig();

    private HikariCPDataSourceConfiguration() throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(Objects.requireNonNull(HikariCPDataSourceConfiguration.class.getClassLoader().getResourceAsStream("configuration.properties"))));
        config.setDriverClassName(properties.getProperty("datasource.driver-class-name"));
        config.setJdbcUrl(properties.getProperty("datasource.url"));
        config.setUsername(properties.getProperty("datasource.username"));
        config.setPassword(properties.getProperty("datasource.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    @SneakyThrows
    public static HikariCPDataSourceConfiguration getInstance() {
        if (instance == null) {
            instance = new HikariCPDataSourceConfiguration();
        }
        return instance;
    }

    public HikariConfig getConfig() {
        return config;
    }
}
