package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistDao extends JpaRepository<Wishlist, String>{


}
