package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    void addWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;
    List<WishlistDTO> getAllLoggedUserWishlists();
    WishlistDTO getWishlistById(Long id);
    void deleteWishlist(Long id) throws IllegalAccessException;
    WishlistDTO addProductToWishlist(Long wishlistId, Long productId) throws IllegalAccessException;
    WishlistDTO removeProductFromWishlist(Long wishlistId, Long productId) throws IllegalAccessException;
}
