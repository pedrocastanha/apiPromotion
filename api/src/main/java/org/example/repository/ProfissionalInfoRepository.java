package org.example.repository;

import org.example.model.ProfissionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfissionalInfoRepository extends JpaRepository<ProfissionalInfo, UUID> {
    // findById (usuarioId) é fornecido pelo JpaRepository
    Optional<ProfissionalInfo> findByRegistroProfissional(String registroProfissional);
    // Adicionar outros métodos de busca específicos se necessário
}

