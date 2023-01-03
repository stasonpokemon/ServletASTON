package com.aston.app.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPDataSource {

    private static HikariCPDataSource instance;

    private final HikariDataSource dataSource;

    private HikariCPDataSource(HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

    public static HikariCPDataSource getInstance(HikariConfig config) {
        if (instance == null) {
            instance = new HikariCPDataSource(config);
        }
        return instance;
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }


}