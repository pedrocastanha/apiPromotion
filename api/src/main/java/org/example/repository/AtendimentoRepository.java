package org.example.repository;

import org.example.model.Agendamento;
import org.example.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> { // Changed UUID to Long
    
    // Find by the associated Agendamento entity (Agendamento ID is Long)
    Optional<Atendimento> findByAgendamento(Agendamento agendamento);
    
    // Find by the associated Agendamento ID (which is Long)
    Optional<Atendimento> findByAgendamentoId(Long agendamentoId); // Changed UUID to Long
    
    // Add other specific find methods if necessary
}

