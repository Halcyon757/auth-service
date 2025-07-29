package ru.t1.homework.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.homework.auth_service.config.JwtSettings;
import ru.t1.homework.auth_service.model.RefreshToken;
import ru.t1.homework.auth_service.model.User;
import ru.t1.homework.auth_service.repository.UserRepository;
import ru.t1.homework.auth_service.security.JwtTokenProvider;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JwtSettings jwtSettings;
    private final RefreshTokenService refreshTokenService;

    @Override
    public JwtResponse signup(SignupRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login is already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        // Создание пользователя
        User user = User.builder()
                .login(request.getLogin())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        // Назначаем роли
        if (request.isPremium()) {
            user.getRoles().clear();
            user.getRoles().add(User.Role.PREMIUM_USER);
        }
        user = userRepository.save(user);

        // Генерация токенов
        String accessToken = tokenProvider.generateAccessToken(user.getLogin());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtSettings.getJwtExpirationMs()
        );
    }

    @Override
    public JwtResponse signin(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        // Создаем новый refresh токен (удаляем старый)
        refreshTokenService.deleteByUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtSettings.getJwtExpirationMs()
        );
    }

    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        // Ротация refresh токена
        refreshTokenService.deleteByUser(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());

        return new JwtResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer",
                jwtSettings.getJwtExpirationMs()
        );
    }

    @Override
    public void signout(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(rt -> refreshTokenService.deleteByUser(rt.getUser()));
    }

    @Override
    public void promoteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + login));

        if (user.getRoles().contains(User.Role.PREMIUM_USER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already premium");
        }
        user.getRoles().add(User.Role.PREMIUM_USER);
        userRepository.save(user);
    }
}