package com.aston.app.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passport {

    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String birthday;
    private String address;
}
