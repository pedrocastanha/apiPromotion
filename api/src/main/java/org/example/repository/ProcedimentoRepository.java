package org.example.repository;

import org.example.model.Clinica;
import org.example.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcedimentoRepository extends JpaRepository<Procedimento, UUID> {
    Optional<Procedimento> findByClinicaAndNome(Clinica clinica, String nome);
    List<Procedimento> findByClinicaAndAtivoIsTrue(Clinica clinica);
    List<Procedimento> findByClinicaId(UUID clinicaId);
    // Adicionar outros métodos de busca conforme necessário
}

