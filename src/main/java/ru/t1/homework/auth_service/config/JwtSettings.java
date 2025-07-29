package ru.t1.homework.auth_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtSettings {
    /**
     * Секрет ключ для подписи JWT (HS256)
     */
    private String secret;

    /**
     * Время жизни access-токена в миллисекундах
     */
    private long jwtExpirationMs;

    /**
     * Время жизни refresh-токена в миллисекундах
     */
    private long refreshTokenDurationMs;
}