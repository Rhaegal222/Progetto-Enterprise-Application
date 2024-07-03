package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    void addOrder(OrderCreateDTO orderCreateDTO) throws IllegalAccessException;
    OrderDTO updateOrder(UUID id, OrderDTO orderDTO) throws IllegalAccessException;
    void deleteOrder(UUID id) throws IllegalAccessException;
    OrderDTO getOrderById(UUID id) throws IllegalAccessException;
    List<OrderDTO> getAllOrders();
}
