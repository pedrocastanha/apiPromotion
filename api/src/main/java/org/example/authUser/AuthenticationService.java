package org.example.authUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.user.User;

import java.io.IOException;

public interface AuthenticationService {
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void login(HttpServletRequest request, HttpServletResponse response) throws IOException;

    User getAuthenticatedUser();

    User getAuthenticatedUserId();
}
