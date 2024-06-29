package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.dao.CartItemDao;
import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.CartItem;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.CartService;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.CartItemDTO;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.dto.creation.CartCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private CartItemDao cartItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(String userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartDao.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).cartItems(Collections.emptyList()).build();
                    cartDao.save(newCart);
                    return newCart;
                });
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addProductToCart(CartCreateDTO cartCreateDTO) {
        User user = userDao.findById(cartCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productDao.findById(cartCreateDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartDao.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    cartDao.save(newCart);
                    return newCart;
                });

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartCreateDTO.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(cartCreateDTO.getQuantity())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cartItemDao.save(cartItem);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(String cartItemId) {
        CartItem cartItem = cartItemDao.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemDao.delete(cartItem);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String cartItemId, int quantity) {
        CartItem cartItem = cartItemDao.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemDao.save(cartItem);
        return convertToDTO(cartItem.getCart());
    }

    private CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .cartItems(cart.getCartItems().stream().map(this::convertToDTO).collect(Collectors.toList()))
                .build();
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
