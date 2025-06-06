package org.example.repository;

import org.example.model.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, UUID> {
    Optional<Clinica> findByCnpj(String cnpj);
    Optional<Clinica> findByEmailContato(String emailContato);
    Optional<Clinica> findById(BigInteger id);
}

