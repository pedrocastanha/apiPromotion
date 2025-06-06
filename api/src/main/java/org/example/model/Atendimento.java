package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for SERIAL columns
    private Long id; // Changed to Long

    @OneToOne(fetch = FetchType.LAZY)
    // The unique constraint is defined in the schema, no need to repeat here unless desired
    @JoinColumn(name = "agendamento_id", nullable = false, unique = true)
    private Agendamento agendamento; // Agendamento ID is Long

    @Lob
    @Column(name = "descricao_sessao", nullable = false, columnDefinition = "TEXT")
    private String descricaoSessao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "produtos_utilizados", columnDefinition = "jsonb")
    private Map<String, Object> produtosUtilizados;

    @Lob
    @Column(name = "observacoes_profissional", columnDefinition = "TEXT")
    private String observacoesProfissional;

    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, columnDefinition = "atendimento_pagamento_status DEFAULT 'PENDENTE'")
    private AtendimentoPagamentoStatus statusPagamento = AtendimentoPagamentoStatus.PENDENTE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

}

