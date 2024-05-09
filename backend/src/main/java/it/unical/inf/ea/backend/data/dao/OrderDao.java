package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDao extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByUser(User user, Pageable pageable);

    Page<Order> findAllByUserAndState(User user, OrderState state, Pageable pageable);

    Page<Order> findAllByProduct_Seller(User user, Pageable pageable);

    Page<Order> findAllByProduct_SellerAndState(User user, OrderState state, Pageable pageable);
}
