package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemDao extends JpaRepository<CartItem, Long> {

}
