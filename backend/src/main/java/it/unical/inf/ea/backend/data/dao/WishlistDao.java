package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WishlistDao extends JpaRepository<Wishlist, String>, JpaSpecificationExecutor<Wishlist> {
    //List<Wishlist> findByOwnerUserEquals(User user);
    List<Wishlist> findAllByUser(User user);
    Wishlist findByUserAndId(User user, Long wishlistId);


}
