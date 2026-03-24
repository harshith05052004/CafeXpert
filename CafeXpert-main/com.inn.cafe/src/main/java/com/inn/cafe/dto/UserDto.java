package com.inn.cafe.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String contactNumber;
    private String email;
    private String password;
    private String status;
    private String role;
    private String otp;
    private String userType;
}
