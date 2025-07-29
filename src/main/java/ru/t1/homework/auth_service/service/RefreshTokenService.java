package ru.t1.homework.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.homework.auth_service.config.JwtSettings;
import ru.t1.homework.auth_service.model.RefreshToken;
import ru.t1.homework.auth_service.model.User;
import ru.t1.homework.auth_service.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtSettings jwtSettings;

    /** Создаёт новый refresh-токен для пользователя */
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(jwtSettings.getRefreshTokenDurationMs()));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    /** Находит RefreshToken по строковому ключу */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /** Проверяет, не истёк ли срок токена */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(FORBIDDEN, "Refresh token was expired. Please login again");
        }
        return token;
    }

    /** Удаляет все токены пользователя (например, при входе или выходе) */
    public int deleteByUser(User user) {
        return refreshTokenRepository.deleteByUser(user);
    }
}
