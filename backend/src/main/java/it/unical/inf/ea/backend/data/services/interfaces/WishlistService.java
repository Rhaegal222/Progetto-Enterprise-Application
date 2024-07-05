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
    WishlistDTO getWishlistById(Long id);
    List<ProductDTO> getProductByWishlistId(Long wishlistId) throws IllegalAccessException;
    WishlistDTO updateWishlist(Long id, WishlistDTO wishlistDTO) throws IllegalAccessException;
    void deleteWishlist(Long id) throws IllegalAccessException;
    void addProductToWishlist(Long wishlistId, Long productId) throws IllegalAccessException;
    void removeProductFromWishlist(Long wishlistId, Long productId) throws IllegalAccessException;
}
