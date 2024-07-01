package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.WishlistDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistDao wishListDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public Wishlist createWishList(User user, Visibility visibility) {
        Wishlist wishlist = new Wishlist();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        wishlist.setUser(loggedUser);
        wishlist.setVisibility(visibility);
        return wishListDao.save(wishlist);
    }

    @Override
    public List<Wishlist> getAllWishlistsByUser(User user) {
        return wishListDao.findAllByUser(user);
    }

    @Override
    public Wishlist getWishListById(User user, Long wishlistId) {
        return wishListDao.findByUserAndId(user, wishlistId);
    }

    @Override
    public void addProductToWishlist(Wishlist wishlist, Product product) {
        wishlist.getProducts().add(product);
        wishListDao.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(Wishlist wishlist, Product product) {
        wishlist.getProducts().remove(product);
        wishListDao.save(wishlist);
    }

}
