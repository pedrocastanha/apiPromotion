package org.example.domain.client;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {
   @EntityGraph(attributePaths = "user")
   List<ClientRecord.clientListDTO> findAllAsDTOByUserId(@Param("userId") Long userId);

   List<Client> findAllByUserId(Long userId);

   @Query("SELECT c FROM Client c WHERE c.user.id = :userId AND c.lastPurchase BETWEEN :startDate AND :endDate")
   List<Client> findAllByUserIdAndLastPurchaseDateBetween(
     @Param("userId") Long userId,
     @Param("startDate") LocalDate startDate,
     @Param("endDate") LocalDate endDate
   );
}
