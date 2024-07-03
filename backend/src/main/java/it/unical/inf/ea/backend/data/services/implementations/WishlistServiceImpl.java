package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.dao.WishlistDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.data.entities.WishlistItem;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishlistDTO;

import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

            Wishlist wishlist = mapToEntity(wishlistCreateDTO);
            wishlist.setUser(loggedUser);

            wishListDao.save(wishlist);

        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WishlistDTO> getAllLoggedUserWishlists() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Logged user cannot be null");
        }
        List<Wishlist> wishlists = wishListDao.findAllByUserId(loggedUser.getId());
        return wishlists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public WishlistDTO getWishlistById(String id) {
        Wishlist wishlist = wishListDao.findById(String.valueOf(id)).orElseThrow(() -> new EntityNotFoundException("Wishlist not found with id: " + id));
        return modelMapper.map(wishlist, WishlistDTO.class);
    }

    @Override
    public void deleteWishlist(String id) throws IllegalAccessException {
        try {
            Wishlist wishlist = wishListDao.findById(String.valueOf(id)).orElseThrow();
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUser().equals(loggedUser))
                throw new IllegalAccessException("User cannot delete wishlist");
            wishListDao.deleteById(String.valueOf(id));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WishlistDTO addProductToWishlist(String wishlistId, String productId) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Wishlist wishlist = wishListDao.findById(String.valueOf(wishlistId)).orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));
            Product product = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));

            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setWishlist(wishlist);
            wishlistItem.setProduct(product);

            wishlist.getItems().add(wishlistItem);

            return mapToDto(wishListDao.save(wishlist));
        } catch (Exception e) {
            throw new RuntimeException("Cannot add product to wishlist");
        }
    }

    @Override
    public void removeProductFromWishlist(String productId, String wishlistId) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Wishlist wishlist = wishListDao.findById(String.valueOf(wishlistId)).orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

            wishlist.getItems().removeIf(wishlistItem -> wishlistItem.getProduct().getId().equals(productId));
        } catch (Exception e) {
            throw new RuntimeException("Cannot remove product from wishlist");
        }
    }


    public Wishlist mapToEntity(WishlistCreateDTO wishlistCreateDTO) { return modelMapper.map(wishlistCreateDTO, Wishlist.class);}
    public WishlistDTO mapToDto(Wishlist wishlist) { return modelMapper.map(wishlist, WishlistDTO.class); }
}











