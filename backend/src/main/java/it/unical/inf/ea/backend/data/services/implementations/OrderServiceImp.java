package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.AddressDao;
import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.dao.PaymentMethodDao;
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
import java.time.LocalDateTime;
import it.unical.inf.ea.backend.dto.enums.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderDao orderDao;
    private final CartDao cartDao;
    private final AddressDao addressDao;
    private final PaymentMethodDao paymentMethodDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    @Transactional
    public void createOrder(OrderCreateDTO orderCreateDTO) throws IllegalAccessException {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Accesso non autorizzato");
            }

            Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Carrello non trovato"));
            if (cart.getCartItems().isEmpty()) {
                throw new IllegalAccessException("Il carrello è vuoto");
            }

            Address address = addressDao.findById(orderCreateDTO.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));

            PaymentMethod paymentMethod = paymentMethodDao.findById(orderCreateDTO.getPaymentMethodId())
                    .orElseThrow(() -> new EntityNotFoundException("Metodo di pagamento non trovato"));

            Order order = Order.builder()
                    .user(loggedUser)
                    .address(address)
                    .paymentMethod(paymentMethod)
                    .items(cart.getCartItems())
                    .status(OrderStatus.CREATED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            orderDao.save(order);

            // Clear the cart after creating the order
            cartDao.delete(cart);
        } catch (Exception e) {
            throw new IllegalAccessException("Impossibile creare l'ordine: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(String id, OrderDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Order order = orderDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !order.getUser().equals(loggedUser)) {
            throw new IllegalAccessException("L'utente non può aggiornare l'ordine");
        }

        modelMapper.map(patch, order);
        order.setUpdatedAt(LocalDateTime.now());

        orderDao.save(order);
        return mapToDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(String id) throws IllegalAccessException {
        Order order = orderDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getRole().equals(UserRole.USER) && !order.getUser().equals(loggedUser)) {
            throw new IllegalAccessException("L'utente non può cancellare l'ordine");
        }
        orderDao.deleteById(id);
    }

    @Override
    public OrderDTO getOrderById(String id) throws IllegalAccessException {
        Order order = orderDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !loggedUser.equals(order.getUser())) {
            throw new IllegalAccessException("L'utente non può ottenere l'ordine");
        }

        return mapToDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderDao.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (!orderDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
