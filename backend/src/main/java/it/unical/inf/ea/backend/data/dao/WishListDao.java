package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WishListDao extends JpaRepository<WishList, String>, JpaSpecificationExecutor<WishList> {
    //List<WishList> findByOwnerUserEquals(User user);
    List<WishList> findAllByUser(User user);
    WishList findByUserAndId(User user, Long wishlistId);


}
