package com.aston.app.pojo.dto;

import lombok.Data;

@Data
public class PassportDTO {

    private String name;
    private String surname;
    private String patronymic;
    private String birthday;
    private String address;
}
