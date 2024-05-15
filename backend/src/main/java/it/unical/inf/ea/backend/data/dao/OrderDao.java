package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.state = ?1")
    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= ?1 AND o.orderDate <= ?2")
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
