package ru.t1.homework.auth_service.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SignupRequest {
    private String login;
    private String email;
    private String password;
    private boolean premium;
}