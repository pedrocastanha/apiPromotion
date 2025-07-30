package org.example.auth;

import org.example.user.UserRecord;

public interface AuthService {
    AuthResponse register(UserRecord.RegisterRequest request);

    AuthResponse authenticate(UserRecord.LoginRequest request);
}
