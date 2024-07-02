package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.OrderService;
import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderDao orderDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public void createOrder(OrderCreateDTO orderCreateDTO) throws IllegalAccessException {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("L'utente loggato non può essere nullo");
            }

            Order order = modelMapper.map(orderCreateDTO, Order.class);
            order.setUser(loggedUser);
            orderDao.save(order);
        } catch (Exception e) {
            throw new IllegalAccessException("Impossibile creare l'ordine");
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(String id, OrderDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id,patch);
        Order order = orderDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !order.getUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("L'utente non può aggiornare l'ordine");
        }

        order.setCart(modelMapper.map(patch.getCart(), Cart.class));
        order.setDeliveryAddress(modelMapper.map(patch.getDeliveryAddress(), Address.class));
        order.setPaymentMethod(modelMapper.map(patch.getPaymentMethod(), PaymentMethod.class));

        orderDao.save(order);
        return mapToDTO(order);
    }

    @Override
    public void deleteOrder(String id) throws IllegalAccessException {
        try{
            Order order = orderDao.findById(id).orElseThrow(EntityNotFoundException::new);
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if(loggedUser.getRole().equals(UserRole.USER) && !order.getUser().getId().equals(loggedUser.getId()))
                throw new IllegalAccessException("Impossibile cancellare l'ordine");
            orderDao.deleteById(id);
        }catch (Exception e){
            throw new IllegalAccessException("Impossibile cancellare l'ordine");
        }
    }

    @Override
    public OrderDTO getOrderById(String id) throws IllegalAccessException {
        Order order = orderDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getUser().getId())) {
            throw new IllegalAccessException("L'utente non può ottenere l'ordine");
        }

        return mapToDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderDao.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (!orderDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
