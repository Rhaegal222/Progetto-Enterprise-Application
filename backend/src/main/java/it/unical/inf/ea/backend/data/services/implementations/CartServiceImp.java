package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CartServiceImp implements CartService {

    private final CartDao cartDao;

    private final CartItemDao cartItemDao;

    private final UserDao userDao;

    private final ProductDao productDao;

    private final JwtContextUtils jwtContextUtils;


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
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User user = userDao.findById(loggedUser.getId())
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

        BigDecimal price = product.isOnSale() ? product.getDiscountedPrice() : product.getProductPrice();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartCreateDTO.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(cartCreateDTO.getQuantity())
                    .productName(product.getTitle())
                    .productPrice(price)
                    .deliveryPrice(product.getDeliveryPrice())
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
                .productName(cartItem.getProductName())
                .productPrice(cartItem.getProductPrice())
                .deliveryPrice(cartItem.getDeliveryPrice())
                .build();
    }
}
