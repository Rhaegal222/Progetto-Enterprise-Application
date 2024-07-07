package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDao extends JpaRepository<Order, UUID> {
    List<Order> findAllByUserId(UUID userId);
}
