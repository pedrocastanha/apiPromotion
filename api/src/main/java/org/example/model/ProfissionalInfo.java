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
@Table(name = "profissionais_info")
public class ProfissionalInfo {

    @Id
    // This field is both PK and FK. We map it directly to the User entity's ID.
    private Long usuarioId; // Changed to Long to match User.id

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Specifies that the PK (usuarioId) is mapped by the User relationship
    @JoinColumn(name = "usuario_id")
    private User usuario; // User ID is Long

    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(name = "registro_profissional", unique = true, length = 50)
    private String registroProfissional;

    @Column(name = "conselho_profissional", length = 20)
    private String conselhoProfissional;

    @Column(name = "duracao_consulta_padrao_min", nullable = false, columnDefinition = "INTEGER DEFAULT 60")
    private Integer duracaoConsultaPadraoMin = 60;

    @Lob
    @Column(name = "biografia_curta", columnDefinition = "TEXT")
    private String biografiaCurta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

}

