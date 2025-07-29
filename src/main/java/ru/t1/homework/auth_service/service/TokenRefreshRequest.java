package ru.t1.homework.auth_service.service;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
