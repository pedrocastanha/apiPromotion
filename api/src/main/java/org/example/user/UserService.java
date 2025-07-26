package org.example.user;

public interface UserService {
    User register(UserRecord.RegisterRequest dto);

    String login(UserRecord.LoginRequest dto);

    User findByEmail(String email);
}
