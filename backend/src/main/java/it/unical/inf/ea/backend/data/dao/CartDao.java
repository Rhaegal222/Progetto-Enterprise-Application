package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CartDao extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findById(Long id);
}
