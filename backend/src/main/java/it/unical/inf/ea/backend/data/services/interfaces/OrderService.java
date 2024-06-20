package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.OrderDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDto);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
    OrderDTO updateOrder(Long id, OrderDTO orderDto);
    void deleteOrder(Long id);
    List<OrderDTO> getOrdersByUserId(Long userId);
    OrderDTO confirmOrder(Long id);
    OrderDTO cancelOrder(Long id);
}
