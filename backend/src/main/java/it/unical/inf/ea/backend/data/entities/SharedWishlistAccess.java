package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shared_wishlist_access")
public class SharedWishlistAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sharedwishlist_seq")
    @SequenceGenerator(name = "sharedwishlist_seq", sequenceName = "sharedwishlist_Sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
