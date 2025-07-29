package ru.t1.homework.auth_service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.t1.homework.auth_service.service.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestBody TokenRefreshRequest request) {
        authService.signout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/promote/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promote(@PathVariable String login) {
        authService.promoteUser(login);
        return ResponseEntity.ok().build();
    }


}
