package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.*;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.OrderService;
import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.OrderItemDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import it.unical.inf.ea.backend.dto.enums.OrderStatus;

import java.util.*;
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
    private final ProductDao productDao;

    @Override
    @Transactional
    public OrderDTO addOrder(OrderCreateDTO orderCreateDTO) throws IllegalAccessException {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Accesso non autorizzato");
            }

            Address address = addressDao.findById(orderCreateDTO.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));

            PaymentMethod paymentMethod = paymentMethodDao.findById(orderCreateDTO.getPaymentMethodId())
                    .orElseThrow(() -> new EntityNotFoundException("Metodo di pagamento non trovato"));

            Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Carrello non trovato"));

            List<CartItem> cartItems = cart.getItems();

            if (cartItems.isEmpty()) {
                throw new IllegalAccessException("Il carrello è vuoto");
            }

            List<OrderItem> Item = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                Product product = productDao.findById(cartItem.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));

                if (cartItem.getQuantity() > product.getQuantity()) {
                    throw new IllegalAccessException("Quantità non disponibile");
                }

                // DA SPOSTARE DOVE SVUOTO IL CARRELLO
                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                productDao.save(product);

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product.getId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPartialCost(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                Item.add(orderItem);
            }

            BigDecimal totalCost = Item.stream().map(OrderItem::getPartialCost).reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = new Order();
            order.setItems(Item);
            order.setTotalCost(totalCost);
            order.setStatus(OrderStatus.CREATED);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setUser(loggedUser);
            order.setAddress(address);
            order.setPaymentMethod(paymentMethod);

            return mapToDTO(orderDao.save(order));
        } catch (Exception e) {
            throw new IllegalAccessException("Impossibile creare l'ordine: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(UUID id, OrderItemDTO patch) throws IllegalAccessException {
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
    public OrderDTO deleteOrder(UUID id) throws IllegalAccessException {
        Order order = orderDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getRole().equals(UserRole.USER) && !order.getUser().equals(loggedUser)) {
            throw new IllegalAccessException("L'utente non può cancellare l'ordine");
        }
       return mapToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(UUID id) throws IllegalAccessException {
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

    @Override
    public List<OrderDTO> getAllLoggedUserOrders() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        List<Order> orders = orderDao.findAllByUserId(loggedUser.getId());
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
