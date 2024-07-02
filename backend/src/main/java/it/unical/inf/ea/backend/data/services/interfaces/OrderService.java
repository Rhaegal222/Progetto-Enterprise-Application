package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;

import java.util.List;

public interface OrderService {

    void createOrder(OrderCreateDTO orderCreateDTO) throws IllegalAccessException;
    OrderDTO updateOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    void deleteOrder(String id) throws IllegalAccessException;
    OrderDTO getOrderById(String id) throws IllegalAccessException;
    List<OrderDTO> getAllOrders();
}
