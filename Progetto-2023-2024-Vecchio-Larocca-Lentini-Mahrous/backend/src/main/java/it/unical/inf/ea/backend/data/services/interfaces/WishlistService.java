package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface WishlistService {
    void addWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;

    List<WishlistDTO> getAllLoggedUserWishlists();

    WishlistDTO getWishlistById(UUID wishlistId) throws IllegalAccessException;

    List<ProductDTO> getProductByWishlistId(UUID wishlistId) throws IllegalAccessException;

    WishlistDTO updateWishlist(UUID wishlistId, WishlistDTO wishlistDTO) throws IllegalAccessException;

    void deleteWishlist(UUID wishlistId) throws IllegalAccessException;

    void addProductToWishlist(UUID wishlistId, Long productId) throws IllegalAccessException;

    void removeProductFromWishlist(UUID wishlistId, Long productId) throws IllegalAccessException;

    void shareWishlist(UUID wishlistId, String Email) throws IllegalAccessException;

    void removeWishlistAccess(UUID wishlistId, String email) throws IllegalAccessException;

    void deleteSharedWishlistAccessByWishlistId(UUID wishlistId) throws IllegalAccessException;
}
