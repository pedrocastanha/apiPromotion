package org.example.domain.client;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
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

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    public void applyUpdate(ClientRecord.updateClientDTO dto) {
        updateIfPresent(dto.name(),        this::setName);
        updateIfPresent(dto.email(),       this::setEmail);
        updateIfPresent(dto.phoneNumber(), this::setPhoneNumber);
        updateIfPresent(dto.product(),     this::setProduct);
        updateIfPresent(dto.amount(),      this::setAmount);
        updateIfPresent(dto.active(),      this::setActive);
        updateIfPresent(dto.lastPurchase(),this::setLastPurchase);
    }
}
