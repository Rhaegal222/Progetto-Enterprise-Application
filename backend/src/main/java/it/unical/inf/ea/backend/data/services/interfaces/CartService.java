package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartCreateDTO;

public interface CartService {

    CartDTO getCartByUserId(String userId);
    CartDTO addProductToCart(CartCreateDTO cartCreateDTO);
    void removeProductFromCart(String cartItemId);
    CartDTO updateCartItem(String cartItemId, int quantity);
}
