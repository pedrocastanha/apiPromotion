package org.example.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class UserRecord{

    @Builder
    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            String phoneNumber,
            @NotBlank @Size(min = 6) String password) {}

    @Builder
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

}
