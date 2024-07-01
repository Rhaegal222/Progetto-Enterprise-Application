package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WishlistService {
    WishlistDTO createWishlist(WishlistCreateDTO wishListCreateDTO);
    List<Wishlist> getAllWishlistsByUser(User user);
    //Optional<Wishlist> getWishlistById(User user, String wishListName);
    WishlistDTO addProductToWishlist(WishlistCreateDTO wishListCreateDTO);
    void removeProductFromWishlist(Wishlist wishlist, Product product);
}
