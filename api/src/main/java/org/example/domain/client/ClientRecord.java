package org.example.domain.client;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    public record ClientResponseDTO(
            Integer id,
            String name,
            String email,
            String phoneNumber,
            String product,
            BigDecimal amount,
            Boolean active,
            Date lastPurchase,
            Timestamp createdAt
    ) {}
}
