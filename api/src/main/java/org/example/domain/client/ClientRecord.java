package org.example.domain.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

public class ClientRecord {
    @Builder
    public record createClientDTO(
            @NotBlank String name,
            @Email String email,
            @NotBlank String phoneNumber,
            String product,
            @NotNull BigDecimal amount,
            Boolean active,
            @PastOrPresent Date lastPurchase,
            @NotNull Long user_id
    ) {}

    @Builder
    public record importClientsDTO(
            @NotNull Long user_id,
            @NotNull String name,
            @NotNull String phoneNumber,
            @NotNull String product,
            @NotNull BigDecimal amount,
            @NotNull Date lastPurchase,
            String email
    ) {}
}
