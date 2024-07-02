package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface WishlistService {
    void createWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;
    List<WishlistDTO> getAllLoggedUserWishlists();
    WishlistDTO getWishlistById(Long id);
    void deleteWishlist(Long id) throws IllegalAccessException;
    void addProductsToWishlist( String productId, Long wishlistId) throws IllegalAccessException;
    void removeProductsFromWishlist(String productId, Long wishlistId) throws IllegalAccessException;
}
