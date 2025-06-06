package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.AgendamentoStatus;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgendamentoResponse {

    private Long id; // Changed from UUID to Long

    // Clinic Info
    private Long clinicaId; // Changed from UUID to Long
    private String clinicaNomeFantasia;

    // Patient Info
    private Long pacienteId; // Changed from UUID to Long
    private String pacienteNome;

    // Professional Info
    private Long profissionalId; // Changed from UUID to Long
    private String profissionalNome;

    // Procedure Info (Optional)
    private Long procedimentoId; // Changed from UUID to Long
    private String procedimentoNome;

    // Scheduling Info
    private OffsetDateTime dataHoraInicio;
    private OffsetDateTime dataHoraFim;
    private AgendamentoStatus status;
    private String observacoes;
    // Add other relevant fields as needed, e.g., recurrence info

    // Metadata
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long criadoPorUsuarioId; // Changed from UUID to Long
    private String criadoPorUsuarioNome; // Optional: Name of the creator

    // Cancellation Info (if applicable)
    private OffsetDateTime dataCancelamento;
    private String motivoCancelamento;
    private Long canceladoPorUsuarioId; // Changed from UUID to Long
    private String canceladoPorUsuarioNome; // Optional: Name of the canceller
}

