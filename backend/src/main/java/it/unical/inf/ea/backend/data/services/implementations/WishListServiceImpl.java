package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.WishListDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.WishList;
import it.unical.inf.ea.backend.data.services.interfaces.WishListService;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {

    private final WishListDao wishListDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public WishList createWishList(User user, Visibility visibility) {
        WishList wishlist = new WishList();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        wishlist.setUser(loggedUser);
        wishlist.setVisibility(visibility);
        return wishListDao.save(wishlist);
    }

    @Override
    public List<WishList> getAllWishlistsByUser(User user) {
        return wishListDao.findAllByUser(user);
    }

    @Override
    public WishList getWishListById(User user, Long wishlistId) {
        return wishListDao.findByUserAndId(user, wishlistId);
    }

    @Override
    public void addProductToWishlist(WishList wishlist, Product product) {
        wishlist.getProducts().add(product);
        wishListDao.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(WishList wishlist, Product product) {
        wishlist.getProducts().remove(product);
        wishListDao.save(wishlist);
    }

}
