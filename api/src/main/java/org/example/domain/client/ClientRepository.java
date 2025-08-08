package org.example.domain.client;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
   @EntityGraph(attributePaths = "user")
   List<ClientRecord.ClientListDTO> findAllByUserId(@Param("userId") Long userId);
}
