package ru.t1.homework.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ru.t1.homework.auth_service.model.User;
import ru.t1.homework.auth_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает UserDetails по логину (username).
     * Spring Security вызывает этот метод при аутентификации.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with login: " + username
                        )
                );
    }
}
