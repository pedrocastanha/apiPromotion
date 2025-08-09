package org.example.domain.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClientRecord {
    @Builder
    public record clientDTO(
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
    public record clientListDTO(
      Integer id,
      String name,
      String email,
      String phoneNumber,
      LocalDate lastPurchase,
      String product,
      BigDecimal amount,
      Boolean active
    ) {}

    @Builder
    public record updateClientDTO(
      String name,
      @Email String email,
      String phoneNumber,
      String product,
      BigDecimal amount,
      Boolean active,
      @PastOrPresent LocalDate lastPurchase
    ) {}
}
