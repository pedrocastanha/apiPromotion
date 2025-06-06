package org.example.repository;

import org.example.model.HorarioTrabalhoProfissional;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HorarioTrabalhoProfissionalRepository extends JpaRepository<HorarioTrabalhoProfissional, UUID> {

    List<HorarioTrabalhoProfissional> findByProfissional(User profissional);

    List<HorarioTrabalhoProfissional> findByProfissionalId(UUID profissionalId);

    Optional<HorarioTrabalhoProfissional> findByProfissionalIdAndDiaSemana(UUID profissionalId, Integer diaSemana);

    List<HorarioTrabalhoProfissional> findByProfissionalIdAndAtivoIsTrueOrderByDiaSemanaAsc(UUID profissionalId);

    // Adicionar outros métodos de busca conforme necessário
}

