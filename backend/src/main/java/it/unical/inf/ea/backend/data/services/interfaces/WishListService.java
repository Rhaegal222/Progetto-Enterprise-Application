package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.WishList;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishListService {
    WishList createWishList(User user, Visibility visibility);
    List<WishList> getAllWishlistsByUser(User user);
    WishList getWishListById(User user, Long wishlistId);
    void addProductToWishlist(WishList wishlist, Product product);
    void removeProductFromWishlist(WishList wishlist, Product product);
}
