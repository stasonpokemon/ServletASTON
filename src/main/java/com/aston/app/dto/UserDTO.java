package com.aston.app.dto;

import com.aston.app.pojo.Passport;
import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String email;
    private Passport passport;
}
