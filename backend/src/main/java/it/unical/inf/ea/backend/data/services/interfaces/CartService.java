package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartItemCreateDTO;

import java.util.UUID;

public interface CartService {

    CartDTO getCartByUserId(UUID userId);
    CartDTO addItemToCart(CartItemCreateDTO cartCreateDTO);
    void removeItemFromCart(Long cartItemId);
    CartDTO updateCartItem(Long cartItemId, int quantity);
}
