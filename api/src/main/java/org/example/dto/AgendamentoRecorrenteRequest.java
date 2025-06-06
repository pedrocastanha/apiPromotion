package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoRecorrenteRequest {
    private Long psicologaId;
    private LocalDateTime dataHoraInicial;
    private int quantidadeRecorrencias;
    private String observacoes;
}
