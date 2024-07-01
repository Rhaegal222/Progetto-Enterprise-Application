package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.dao.WishlistDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final UserDao userDao;
    private final WishlistDao wishListDao;
    private final ModelMapper modelMapper;
    private final ProductDao productDao;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public void createWishlist(WishlistCreateDTO wishlistCreateDTO) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Wishlist wishlist = modelMapper.map(wishlistCreateDTO, Wishlist.class);
            wishlist.setUser(loggedUser);
            wishListDao.save(wishlist);

        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<Wishlist> getAllWishlistsByUser(User user) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        List<Wishlist> wishlists = wishListDao.findByUser(loggedUser);
        return wishlists.stream()
                .filter(wishlist -> wishlist.getUser().equals(user))
                .toList();

    }

//    @Override
//    public Optional<Wishlist> getWishlistById(User user, String wishListName) {
//        return wishListDao.findByUserAndName(user, wishListName);
//    }

//    @Override
//    public WishlistDTO addProductToWishlist(WishlistDTO wishlistDTO) {
//        try {
//            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
//
//            if (loggedUser == null) {
//                throw new IllegalStateException("Logged user cannot be null");
//            }
//            Product product = productDao.findById(wishlistDTO.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//
//            Wishlist wishlist = wishListDao.findByUser(loggedUser)
//                    .stream()
//                    .filter(wishList -> wishList.getWishlistName().equals(wishlistDTO.getWishListName()))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Wishlist not found"));
//            if (!wishlist.getProducts().contains(product)) {
//                wishlist.getProducts().add(product);
//                wishListDao.save(wishlist);
//            }
//            return modelMapper.map(wishlist, WishlistDTO.class);
//        } catch (IllegalStateException e) {
//            throw new RuntimeException(e);
//        }
//    }





        @Override
    public void removeProductFromWishlist(Wishlist wishlist, Product product) {
        wishlist.getProducts().remove(product);
        wishListDao.save(wishlist);
    }

}
