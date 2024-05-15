package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.data.services.interfaces.OrderService;
import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao;

    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDto) {
        Order order = modelMapper.map(orderDto, Order.class);
        order = orderDao.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderDao.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderDao.findById(Long.valueOf(String.valueOf(id))).orElse(null);
        return order != null ? modelMapper.map(order, OrderDTO.class) : null;
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDto) {
        Order existingOrder = orderDao.findById(Long.valueOf(String.valueOf(id))).orElse(null);

        if (existingOrder != null) {
            existingOrder.setOrderUpdateDate(LocalDateTime.now());
            existingOrder = orderDao.save(existingOrder);
            return modelMapper.map(existingOrder, OrderDTO.class);
        }

        return null;
    }



    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderDao.deleteById(Long.valueOf(String.valueOf(id)));
    }




    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderDao.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO confirmOrder(Long id) {
        Order order = orderDao.findById(id).orElse(null);

        if (order != null) {
            order.setState(OrderState.valueOf("Confirmed"));
            order = orderDao.save(order);
            return modelMapper.map(order, OrderDTO.class);
        }

        return null;
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        Order order = orderDao.findById(id).orElse(null);

        if (order != null) {
            order.setState(OrderState.valueOf("Canceled"));
            order = orderDao.save(order);
            return modelMapper.map(order, OrderDTO.class);

        }
        return null;
    }


}
