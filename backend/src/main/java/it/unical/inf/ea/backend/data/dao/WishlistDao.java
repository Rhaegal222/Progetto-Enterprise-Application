package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WishlistDao extends JpaRepository<Wishlist, Long>{
    List<Wishlist> findAllByUserId(UUID userId);
}
