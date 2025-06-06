package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for SERIAL columns
    private Long id; // Changed to Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id") // FK column name from schema
    private Clinica clinica; // Clinica ID is Long

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "endereco_logradouro", length = 255)
    private String enderecoLogradouro;

    @Column(name = "endereco_numero", length = 50)
    private String enderecoNumero;

    @Column(name = "endereco_complemento", length = 100)
    private String enderecoComplemento;

    @Column(name = "endereco_bairro", length = 100)
    private String enderecoBairro;

    @Column(name = "endereco_cidade", length = 100)
    private String enderecoCidade;

    @Column(name = "endereco_uf", length = 2)
    private String enderecoUf;

    @Column(name = "endereco_cep", length = 8)
    private String enderecoCep;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PacienteInfo pacienteInfo; // Maps back from PacienteInfo.usuario (which should be User type)

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfissionalInfo profissionalInfo; // Maps back from ProfissionalInfo.usuario

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentosComoPaciente; // Maps back from Agendamento.paciente

    @OneToMany(mappedBy = "profissional", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentosComoProfissional; // Maps back from Agendamento.profissional

    @OneToMany(mappedBy = "criadoPorUsuario", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentosCriados; // Maps back from Agendamento.criadoPorUsuario

    @OneToMany(mappedBy = "canceladoPorUsuario", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentosCancelados; // Maps back from Agendamento.canceladoPorUsuario

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioTrabalhoProfissional> horariosTrabalho; // Maps back from HorarioTrabalhoProfissional.profissional

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExcecaoHorarioProfissional> excecoesHorario; // Maps back from ExcecaoHorarioProfissional.profissional

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notificacao> notificacoes; // Maps back from Notificacao.usuario

}

