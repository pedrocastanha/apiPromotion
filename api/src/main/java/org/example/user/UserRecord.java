package org.example.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRecord {
    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            String phoneNumber,
            @NotBlank @Size(min = 6) String password) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }
}
