package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.basics.OrderBasicDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderCreateDTO orderDTO) throws IllegalAccessException;
    OrderDTO updateOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    void deleteOrder(String id) throws IllegalAccessException;
    OrderDTO getOrderById(String id) throws IllegalAccessException;
    Page<OrderBasicDTO> findAllMyOrdersByState(Pageable pageable, OrderState state);

    Page<OrderBasicDTO> findAllMySellerOrdersByState(Pageable pageable, OrderState state);
}
