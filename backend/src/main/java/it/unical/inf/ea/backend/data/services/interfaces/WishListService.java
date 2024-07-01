package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.dto.WishListDTO;
import it.unical.inf.ea.backend.dto.creation.WishListCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WishlistService {
    WishListDTO createWishlist(WishListCreateDTO wishListCreateDTO);
    List<Wishlist> getAllWishlistsByUser(User user);
    Optional<Wishlist> getWishListById(User user, String wishListName);
    WishListDTO addProductToWishlist(WishListCreateDTO wishListCreateDTO);
    void removeProductFromWishlist(Wishlist wishlist, Product product);
}
