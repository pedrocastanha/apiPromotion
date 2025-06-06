package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pacientes_info")
public class PacienteInfo {

    @Id
    // This field is both PK and FK. We map it directly to the User entity's ID.
    private Long usuarioId; // Changed to Long to match User.id

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Specifies that the PK (usuarioId) is mapped by the User relationship
    @JoinColumn(name = "usuario_id")
    private User usuario; // User ID is Long

    @Lob
    @Column(name = "historico_medico_resumo", columnDefinition = "TEXT")
    private String historicoMedicoResumo;

    @Lob
    @Column(name = "observacoes_cadastro", columnDefinition = "TEXT")
    private String observacoesCadastro;

    @Column(name = "plano_saude", length = 100)
    private String planoSaude;

    @Column(name = "numero_carteirinha", length = 100)
    private String numeroCarteirinha;

    @Column(name = "como_conheceu", length = 255)
    private String comoConheceu;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

}

