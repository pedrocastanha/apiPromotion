package org.example.domain.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
   @Query("select c from Client c join fetch c.user u where u.id = :userId")
   List<Client> findAllByUserIdFetch(@Param("userId") Long userId);
}
