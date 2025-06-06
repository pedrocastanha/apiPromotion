package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(max = 100, message = "Nome não pode exceder 100 caracteres")
    private String nome;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    @Size(max = 150, message = "Email não pode exceder 150 caracteres")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres") // Example validation
    private String senha;

    @Size(max = 20, message = "Telefone não pode exceder 20 caracteres")
    private String telefone;

    // Removed frequenciaAgendamento as it's no longer directly on the usuarios table
    // This concept might be handled elsewhere if needed (e.g., PacienteInfo or specific settings)
}

