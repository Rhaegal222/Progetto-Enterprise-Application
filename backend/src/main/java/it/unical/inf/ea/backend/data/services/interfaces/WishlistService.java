package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    Wishlist createWishList(User user, Visibility visibility);
    List<Wishlist> getAllWishlistsByUser(User user);
    Wishlist getWishListById(User user, Long wishlistId);
    void addProductToWishlist(Wishlist wishlist, Product product);
    void removeProductFromWishlist(Wishlist wishlist, Product product);
}
