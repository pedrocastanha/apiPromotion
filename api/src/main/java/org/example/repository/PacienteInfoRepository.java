package org.example.repository;

import org.example.model.PacienteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PacienteInfoRepository extends JpaRepository<PacienteInfo, UUID> {
    // findById (usuarioId) é fornecido pelo JpaRepository
    // Adicionar outros métodos de busca específicos se necessário
}

