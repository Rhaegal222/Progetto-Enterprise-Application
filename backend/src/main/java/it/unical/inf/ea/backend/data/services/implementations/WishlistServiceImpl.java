package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.dao.WishlistDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishListDTO;
import it.unical.inf.ea.backend.dto.creation.WishListCreateDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final UserDao userDao;
    private final WishlistDao wishListDao;
    private final ModelMapper modelMapper;
    private final ProductDao productDao;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public WishListDTO createWishlist(WishListCreateDTO wishListCreateDTO) {
        Wishlist wishlist = new Wishlist();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
//        User user = userDao.findById(wishListCreateDTO.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productDao.findById(wishListCreateDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        wishlist.setUser(loggedUser);
        wishlist.setWishlistName(wishListCreateDTO.getWishListName());
        wishlist.setVisibility(wishListCreateDTO.getVisibility());
        wishlist.getProducts().add(product);
        return modelMapper.map(wishListDao.save(wishlist), WishListDTO.class);
    }

    @Override
    public List<Wishlist> getAllWishlistsByUser(User user) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        List<Wishlist> wishlists = wishListDao.findByUser(loggedUser);
        return wishlists.stream()
                .filter(wishlist -> wishlist.getUser().equals(user))
                .toList();

    }

    @Override
    public Optional<Wishlist> getWishListById(User user, String wishListName) {
        return wishListDao.findByUserAndName(user, wishListName);
    }

    @Override

    public WishListDTO addProductToWishlist(WishListCreateDTO wishListCreateDTO) {
        User user = userDao.findById(wishListCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productDao.findById(wishListCreateDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = wishListDao.findByUser(user)
                .stream()
                .filter(wishList -> wishList.getWishlistName().equals(wishListCreateDTO.getWishListName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
        if (!wishlist.getProducts().contains(product)) {
            wishlist.getProducts().add(product);
            wishListDao.save(wishlist);
        }
        return modelMapper.map(wishlist, WishListDTO.class);
    }





        @Override
    public void removeProductFromWishlist(Wishlist wishlist, Product product) {
        wishlist.getProducts().remove(product);
        wishListDao.save(wishlist);
    }

}
