package com.aston.app.dao;

import com.aston.app.exception.DBConnectionException;
import com.aston.app.pojo.Passport;

import java.util.Optional;

public interface PassportDAO {

    boolean saveByUserId(Long userId, Passport passport) throws DBConnectionException;

    Optional<Passport> findPassportByUserId(Long userId) throws DBConnectionException;

    boolean updateByUserId(Long userId, Passport passport) throws DBConnectionException;
}
