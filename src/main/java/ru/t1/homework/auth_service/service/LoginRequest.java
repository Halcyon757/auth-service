package ru.t1.homework.auth_service.service;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}