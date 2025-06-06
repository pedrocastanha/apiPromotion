package org.example.model;

// Enum definido conforme a migração V4
public enum NotificacaoCanal {
    EMAIL,
    WHATSAPP,
    SMS, // Embora não implementado inicialmente, previsto no schema
    SISTEMA // Para notificações internas na UI
}

