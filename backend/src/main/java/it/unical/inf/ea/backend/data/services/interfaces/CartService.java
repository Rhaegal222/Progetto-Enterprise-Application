package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartItemCreateDTO;

public interface CartService {

    CartDTO getCartByUserId(String userId);
    CartDTO addItemToCart(CartItemCreateDTO cartCreateDTO);
    void removeItemFromCart(String cartItemId);
    CartDTO updateCartItem(String cartItemId, int quantity);
}
