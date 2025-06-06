package org.example.model;

// Enum definido conforme a migração V3
public enum UserRole {
    DONO_CLINICA,
    ATENDENTE,
    PROFISSIONAL,
    PACIENTE,
    ADMIN // Mantido para compatibilidade com V2 e possível superusuário
}

