package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "payment_methods")
@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    @Length(min = 19, max = 19)
    private String cardNumber;

    @Column(nullable = false)
    private String expireMonth;

    @Column(nullable = false)
    private String expireYear;

    @Column(nullable = false)
    private String owner;

    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
