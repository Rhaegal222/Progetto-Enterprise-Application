package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_cart", nullable = false)
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_user", nullable = false)
    private User user;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_address",nullable = false)
    private Address deliveryAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_payment",nullable = false)
    private PaymentMethod paymentMethod;

}
