package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.SharedWishlistAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SharedWishlistAccessDao extends JpaRepository<SharedWishlistAccess, Long> {
    Optional<SharedWishlistAccess> findByWishlistIdAndUserId(UUID wishlist_id, UUID user_id);
     void deleteByWishlistId(UUID wishlist_id);

}
