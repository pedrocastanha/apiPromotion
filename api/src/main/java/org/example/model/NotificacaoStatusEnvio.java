package org.example.model;

// Enum definido conforme a migração V4
public enum NotificacaoStatusEnvio {
    PENDENTE,
    ENVIADO,
    FALHA,
    LIDO // Status para rastrear se o usuário visualizou (útil para notificações no sistema)
}

