package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelamentoRequest {
    private Long agendamentoId;
    private String motivo;
}
