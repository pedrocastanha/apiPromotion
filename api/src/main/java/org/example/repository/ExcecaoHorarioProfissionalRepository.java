package org.example.repository;

import org.example.model.ExcecaoHorarioProfissional;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExcecaoHorarioProfissionalRepository extends JpaRepository<ExcecaoHorarioProfissional, UUID> {

    List<ExcecaoHorarioProfissional> findByProfissional(User profissional);

    List<ExcecaoHorarioProfissional> findByProfissionalId(UUID profissionalId);

    Optional<ExcecaoHorarioProfissional> findByProfissionalIdAndData(UUID profissionalId, LocalDate data);

    List<ExcecaoHorarioProfissional> findByProfissionalIdAndDataBetweenOrderByDataAsc(
            UUID profissionalId, LocalDate inicio, LocalDate fim);

    // Adicionar outros métodos de busca conforme necessário
}

