package org.example.domain.auth;

import org.example.domain.user.UserRecord;

public interface AuthService {
    AuthResponse register(UserRecord.RegisterRequest request);

    AuthResponse authenticate(UserRecord.LoginRequest request);
}
