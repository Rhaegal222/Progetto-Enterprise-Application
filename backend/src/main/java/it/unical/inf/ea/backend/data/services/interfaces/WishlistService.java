package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    void createWishlist(WishlistCreateDTO wishListCreateDTO) throws IllegalAccessException;
    List<Wishlist> getAllWishlistsByUser(User user);
    //WishlistDTO addProductToWishlist(WishlistDTO wishlistDTO);
    void removeProductFromWishlist(Wishlist wishlist, Product product);
}
