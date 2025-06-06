package org.example.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AGENDAMENTO")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relação com paciente (usuário)
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private User paciente;

    // Relação com psicóloga (usuário)
    @ManyToOne
    @JoinColumn(name = "psicologa_id", nullable = false)
    private User psicologa;

    @Column(nullable = false)
    private LocalDateTime horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String observacoes;
    
    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;
    
    @Column(name = "cancelado_por")
    private String canceladoPor; // "PACIENTE" ou "PSICOLOGA"
}
