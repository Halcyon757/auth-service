package ru.t1.homework.auth_service.service;

public interface AuthService {
    JwtResponse signup(SignupRequest request);
    JwtResponse signin(LoginRequest request);
    JwtResponse refreshToken(TokenRefreshRequest request);
    void signout(String refreshToken);

    /**
     * Промоутит пользователя в PREMIUM_USER. Доступно ADMIN.
     */
    void promoteUser(String login);
}