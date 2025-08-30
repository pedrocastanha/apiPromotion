package org.example.domain.user;

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

   public record ChatMessageRequest(
     Integer userId,
     String message
   ) {}

   public record ChatApiRequest(
     @NotBlank String company_name,
     @NotBlank String company_type,
     @NotBlank String message) {
   }

   public record ChatApiResponse(
     String response
   ){}
}
