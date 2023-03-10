package com.aston.app.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String email;
    private PassportDTO passport;
}
