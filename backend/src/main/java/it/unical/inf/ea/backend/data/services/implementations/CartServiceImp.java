package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.CartItem;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.dao.CartItemDao;
import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.services.interfaces.CartService;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartItemCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public CartDTO getCartByUserId(UUID userId) {
        return cartDao.findByUserId(userId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Override
    public CartDTO getCartForLoggedUser() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }
        return cartDao.findByUser(loggedUser)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Override
    public CartDTO addItemToCart(CartItemCreateDTO cartItemCreate) {

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }

        Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        CartItem cartItem = modelMapper.map(cartItemCreate, CartItem.class);
        cartItem.setCart(cart);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            cart.getItems().remove(existingItem.get());
            existingItem.get().setQuantity(existingItem.get().getQuantity() + cartItem.getQuantity());
            cart.getItems().add(existingItem.get());
        } else {
            cart.getItems().add(cartItem);
        }
        return mapToDTO(cartDao.save(cart));
    }

    @Override
    public CartDTO removeItemFromCart(UUID cartItemId) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }

        Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            cart.getItems().remove(existingItem.get());
            return mapToDTO(cartDao.save(cart));
        } else {
            throw new EntityNotFoundException("Product not found in cart");
        }
    }

    @Override
    @Transactional
    public void clearCart() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }
        cartDao.findByUser(loggedUser).ifPresent(cart -> {
            List<CartItem> items = cart.getItems();
            items.forEach(cartItem -> cartItemDao.deleteById(cartItem.getId()));
            items.clear();
        });
    }

    Cart mapToEntity(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }
    CartDTO mapToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }
}

