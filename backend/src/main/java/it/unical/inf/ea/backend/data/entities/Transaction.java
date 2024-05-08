package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.data.entities.embedded.CustomMoney;
import it.unical.inf.ea.backend.dto.enums.TransactionState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction")
@Entity

public class Transaction {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    //@CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationTime;

    @Embedded
    @Column(nullable = false)
    private CustomMoney amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionState transactionState;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentMethodOwner;

    @OneToOne(mappedBy = "transaction",fetch = FetchType.LAZY)
    private Order order;
}
