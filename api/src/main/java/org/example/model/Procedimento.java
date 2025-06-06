package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "procedimentos",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"clinica_id", "nome"})})
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for SERIAL columns
    private Long id; // Changed to Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica; // Clinica ID is Long

    @Column(nullable = false, length = 255)
    private String nome;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "duracao_estimada_min", nullable = false)
    private Integer duracaoEstimadaMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    // Relationship
    @OneToMany(mappedBy = "procedimento", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentos; // Maps back from Agendamento.procedimento

}

