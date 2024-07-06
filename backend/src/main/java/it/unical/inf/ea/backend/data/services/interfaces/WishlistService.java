package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.AddressDTO;
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
    WishlistDTO getWishlistById(String wishlistId);
    List<ProductDTO> getProductByWishlistId(String wishlistId) throws IllegalAccessException;
    WishlistDTO updateWishlist(String wishlistId, WishlistDTO wishlistDTO) throws IllegalAccessException;
    void deleteWishlist(String wishlistId) throws IllegalAccessException;
    void addProductToWishlist(String wishlistId, Long productId) throws IllegalAccessException;
    void removeProductFromWishlist(String wishlistId, Long productId) throws IllegalAccessException;
}
