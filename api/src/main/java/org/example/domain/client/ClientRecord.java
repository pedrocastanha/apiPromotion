package org.example.domain.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClientRecord {
    @Builder
    public record createClientDTO(
            @NotBlank String name,
            @Email String email,
            @NotBlank String phoneNumber,
            String product,
            @NotNull BigDecimal amount,
            Boolean active,
            @PastOrPresent LocalDate lastPurchase,
            @NotNull Long user_id
    ) {}

    @Builder
    public record importClientsDTO(
            @NotNull Long user_id,
            @NotNull String name,
            @NotNull String phoneNumber,
            @NotNull String product,
            @NotNull BigDecimal amount,
            @NotNull LocalDate lastPurchase,
            String email
    ) {}

    @Builder
    public record ClientListDTO(
      Integer userId,
      String name,
      String email,
      String phoneNumber,
      String product,
      BigDecimal amount,
      String lastPurchase // dd/MM/yyyy
    ) {
        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        public static ClientListDTO of(Client c) {
            String date = c.getLastPurchase() != null ? c.getLastPurchase().format(FMT) : null;
            return new ClientListDTO(
              c.getUser().getId(),
              c.getName(),
              c.getEmail(),
              c.getPhoneNumber(),
              c.getProduct(),
              c.getAmount(),
              date
            );
        }
    }
}
