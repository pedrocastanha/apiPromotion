package org.example.domain.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.UserRecord;
import org.example.exception.CustomExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5034")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserRecord.LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e) {
            throw new CustomExceptions.AuthenticationException("Credenciais inválidas fornecidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRecord.RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            throw new CustomExceptions.DataConflictException("E-mail ou nome de usuário já registrado");
        }
    }
}