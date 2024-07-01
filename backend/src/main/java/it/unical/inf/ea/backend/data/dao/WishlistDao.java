package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistDao extends JpaRepository<Wishlist, String>{
    //List<Wishlist> findByOwnerUserEquals(User user);
    List<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUserAndName(User user, String wishlistName);


}
