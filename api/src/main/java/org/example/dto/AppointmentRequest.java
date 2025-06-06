package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {
    private Long psicologaId;
    private LocalDateTime horario;
    private String observacoes;
}

