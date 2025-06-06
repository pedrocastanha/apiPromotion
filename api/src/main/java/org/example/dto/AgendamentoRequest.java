package org.example.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AgendamentoRequest {

    // Changed from UUID to Long
    @NotNull(message = "ID do profissional não pode ser nulo")
    private Long profissionalId;

    @NotNull(message = "Horário não pode ser nulo")
    @FutureOrPresent(message = "Horário do agendamento deve ser no presente ou futuro")
    private OffsetDateTime dataHoraInicio;

    // Optional: Link to a specific procedure
    // Changed from UUID to Long
    private Long procedimentoId;

    private String observacoes;

    // Implicitly needed by the service layer, will be handled there
    // private Long pacienteId; 
    // private Long clinicaId;
    // private Long criadoPorUsuarioId; 
}

