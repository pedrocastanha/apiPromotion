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
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for SERIAL columns
    private Long id; // Changed to Long

    // Optional relationship with Agendamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id") // Nullable, ON DELETE SET NULL is defined in DB
    private Agendamento agendamento; // Agendamento ID is Long

    // User receiving the notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // ON DELETE CASCADE is defined in DB
    private User usuario; // User ID is Long

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_canal", nullable = false, columnDefinition = "notificacao_canal")
    private NotificacaoCanal tipoCanal;

    @Column(name = "evento_trigger", nullable = false, length = 100)
    private String eventoTrigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_envio", nullable = false, columnDefinition = "notificacao_status_envio DEFAULT 'PENDENTE'")
    private NotificacaoStatusEnvio statusEnvio = NotificacaoStatusEnvio.PENDENTE;

    @Column(name = "data_agendada_envio", nullable = false)
    private OffsetDateTime dataAgendadaEnvio;

    @Column(name = "data_efetiva_envio")
    private OffsetDateTime dataEfetivaEnvio;

    @Column(length = 255)
    private String titulo;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "id_externo_mensagem", length = 255)
    private String idExternoMensagem;

    @Lob
    @Column(name = "resposta_erro", columnDefinition = "TEXT")
    private String respostaErro;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

}

