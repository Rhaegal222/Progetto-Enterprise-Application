package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.CartItem;
import it.unical.inf.ea.backend.data.entities.Product;
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

import java.util.List;
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
        User user = userDao.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartDao.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO addItemToCart(CartItemCreateDTO cartItemCreateDTO) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
            Product product = productDao.findById(cartItemCreateDTO.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemCreateDTO.getQuantity());

            cart.getCartItems().add(cartItem);

            return mapToDTO(cartDao.save(cart));
        } catch (Exception e) {
            throw new RuntimeException("Cannot add item to cart");
        }
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
            List<CartItem> cartItems = cart.getCartItems();

            for (CartItem cartItem : cartItems) {
                if (cartItem.getId().equals(cartItemId)) {
                    cartItems.remove(cartItem);
                    cartItemDao.deleteById(cartItemId);
                    break;
                }
            }

            cart.setCartItems(cartItems);
            cartDao.save(cart);
        } catch (Exception e) {
            throw new RuntimeException("Cannot remove item from cart");
        }
    }

    @Override
    public CartDTO updateCartItem(Long cartItemId, int quantity) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Cart cart = cartDao.findByUser(loggedUser).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
            List<CartItem> cartItems = cart.getCartItems();

            for (CartItem cartItem : cartItems) {
                if (cartItem.getId().equals(cartItemId)) {
                    cartItem.setQuantity(quantity);
                    cartItemDao.save(cartItem);
                    break;
                }
            }

            cart.setCartItems(cartItems);
            return mapToDTO(cartDao.save(cart));
        } catch (Exception e) {
            throw new RuntimeException("Cannot update cart item");
        }
    }

    Cart mapToEntity(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }
    CartDTO mapToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }
}

