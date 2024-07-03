package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    void createWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;
    List<WishlistDTO> getAllLoggedUserWishlists();
    WishlistDTO getWishlistById(String id);
    void deleteWishlist(String id) throws IllegalAccessException;
    WishlistDTO addProductToWishlist(String productId, String wishlistId) throws IllegalAccessException;
    WishlistDTO removeProductFromWishlist(String wishlistId, String productId) throws IllegalAccessException;
}
