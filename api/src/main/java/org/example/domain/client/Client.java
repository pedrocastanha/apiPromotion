package org.example.domain.client;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "phonenumber", unique = true, length = 14)
    private String phoneNumber;

    @Column(name = "product")
    private String product;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "last_purchase")
    private LocalDate lastPurchase;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    public Client(ClientRecord.importClientsDTO dto, User userEntity) {
        this.name = dto.name();
        this.email = dto.email();
        this.phoneNumber = dto.phoneNumber();
        this.product = dto.product();
        this.amount = dto.amount();
        this.lastPurchase = dto.lastPurchase();
        this.user = userEntity;
    }
}
