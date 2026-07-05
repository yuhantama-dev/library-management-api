package dev.yuhantama.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.yuhantama.library.auth.AuthResponse;
import dev.yuhantama.library.auth.LoginRequest;
import dev.yuhantama.library.auth.RegisterRequest;
import dev.yuhantama.library.entity.User;
import dev.yuhantama.library.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User created = authService.register(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        // extract email and role from request (we can also decode token)
        return ResponseEntity.ok(new AuthResponse(token, request.getEmail(), "USER"));
    }
}
