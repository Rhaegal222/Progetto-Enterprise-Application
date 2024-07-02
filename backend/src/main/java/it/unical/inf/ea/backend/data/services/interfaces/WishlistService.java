package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface WishlistService {
    void createWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;
    List<WishlistDTO> getAllWishlists();
    WishlistDTO getWishlistById(Long id);
    void deleteWishlist(String id) throws IllegalAccessException;
    void addProductsToWishlist(Set<String> productIds, Long wishlistId) throws IllegalAccessException;
    void removeProductsFromWishlist(Set<String> productIds, Long wishlistId) throws IllegalAccessException;
}
