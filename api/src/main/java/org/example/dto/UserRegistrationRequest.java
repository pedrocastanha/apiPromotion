package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.model.UserRole; // Import the UserRole enum

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
// Renamed and expanded RegistrationRequest
public class UserRegistrationRequest {

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    // Role is now mandatory for registration via API
    private UserRole role;

    // Optional: Clinica ID, required for staff roles, optional for patients
    private UUID clinicaId;

    // Optional fields, can be added later via profile update
    @Size(max = 11, message = "CPF deve ter 11 caracteres")
    private String cpf;
    private LocalDate dataNascimento;

    // Optional address fields
    @Size(max = 255) private String enderecoLogradouro;
    @Size(max = 50) private String enderecoNumero;
    @Size(max = 100) private String enderecoComplemento;
    @Size(max = 100) private String enderecoBairro;
    @Size(max = 100) private String enderecoCidade;
    @Size(max = 2) private String enderecoUf;
    @Size(max = 8) private String enderecoCep;

    // --- Role-Specific Fields (Consider using separate DTOs or validation groups) ---

    // For PROFISSIONAL role
    private String especialidade; // Required if role is PROFISSIONAL
    private String registroProfissional;
    private String conselhoProfissional;
    private Integer duracaoConsultaPadraoMin;
    private String biografiaCurta;

    // For PACIENTE role (Optional during registration)
    private String historicoMedicoResumo;
    private String observacoesCadastro;
    private String planoSaude;
    private String numeroCarteirinha;
    private String comoConheceu;

    // Note: Removed frequenciaAgendamento as it's not a user property anymore
}

