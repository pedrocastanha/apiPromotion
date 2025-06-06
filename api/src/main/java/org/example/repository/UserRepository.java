package org.example.repository;

import org.example.model.User;
import org.example.model.UserRole; // Import UserRole if needed for custom queries
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List; // Import List if needed for custom queries
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByClinicaIdAndRole(Long clinicaId, UserRole role);
    Optional<User> findByCpf(String cpf);
    Optional<User> findById(BigInteger id);
}

