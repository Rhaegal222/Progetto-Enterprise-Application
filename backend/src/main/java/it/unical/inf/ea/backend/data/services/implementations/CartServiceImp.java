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

import java.util.Optional;
import java.util.Set;
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

        CartItem cartItem = modelMapper.map(cartItemCreate, CartItem.class);

        Optional<Cart> cartOptional = cartDao.findByUserId(loggedUser.getId());
        Cart cart;
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
            Set<CartItem> items = cart.getItems();
            items.add(cartItem);
        } else {
            cart = new Cart();
            cart.setUser(loggedUser);
            cart.getItems().add(cartItem);
        }
        cartDao.save(cart);
        return mapToDTO(cart);
    }

    @Override
    public void removeItemFromCart(UUID cartItemId) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }
        cartItemDao.findById(cartItemId).ifPresent(cartItem -> {
            if (cartItem.getCart().getUser().getId().equals(loggedUser.getId())) {
                cartItemDao.deleteById(cartItemId);
            } else {
                throw new IllegalStateException("Accesso non autorizzato");
            }
        });
    }

    @Override
    @Transactional
    public void clearCart() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Accesso non autorizzato");
        }
        cartDao.findByUser(loggedUser).ifPresent(cart -> {
            Set<CartItem> items = cart.getItems();
            items.forEach(cartItem -> cartItemDao.deleteById(cartItem.getId()));
            items.clear();
        });
    }

    Cart mapToEntity(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }
    CartDTO mapToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }
}

