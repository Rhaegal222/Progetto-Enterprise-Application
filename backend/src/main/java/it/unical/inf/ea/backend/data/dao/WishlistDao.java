package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistDao extends JpaRepository<Wishlist, String>{
    List<Wishlist> findAllByUserId(String userId);
}
