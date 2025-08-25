package org.example.domain.company;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class Company {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @Column(name = "name", nullable = false, unique = true)
   private String name;

   @Column(name = "type", nullable = false)
   private String type;

   @Column(name = "description")
   private String description;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

}