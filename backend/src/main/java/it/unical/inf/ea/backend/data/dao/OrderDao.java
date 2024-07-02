package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order>, PagingAndSortingRepository<Order, String> {
    List<Order> findByUserEquals(User user);
}
