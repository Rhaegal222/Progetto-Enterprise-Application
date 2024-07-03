package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.WishlistVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlist_seq")
    @SequenceGenerator(name = "wishlist_seq", sequenceName = "wishlist_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String wishlistName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishlistVisibility visibility;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wishlist_products", joinColumns = @JoinColumn(name = "wishlist_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
