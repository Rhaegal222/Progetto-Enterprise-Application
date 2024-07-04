package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.CartItem;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartItemCreateDTO;

import java.util.UUID;

public interface CartService {
    CartDTO getCartByUserId(UUID userId);
    CartDTO getCartForLoggedUser();
    CartDTO addItemToCart(CartItemCreateDTO cartItem);
    void removeItemFromCart(UUID cartItemId);
    void clearCart();
}
