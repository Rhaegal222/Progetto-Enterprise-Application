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
            wishlist.setUserId(loggedUser.getId());

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
    public WishlistDTO getWishlistById(Long id) {
        Wishlist wishlist = wishListDao.findById(String.valueOf(id)).orElseThrow(() -> new EntityNotFoundException("Wishlist not found with id: " + id));
        return modelMapper.map(wishlist, WishlistDTO.class);
    }

    @Override
    public void deleteWishlist(String id) throws IllegalAccessException {
        try {
            Wishlist wishlist = wishListDao.findById(id).orElseThrow();
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUserId().equals(loggedUser.getId()))
                throw new IllegalAccessException("User cannot delete wishlist");
            wishListDao.deleteById(id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProductsToWishlist(Set<String> productIds, Long wishlistId) throws IllegalAccessException, EntityNotFoundException, IllegalStateException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Logged user cannot be null");
        }
        Wishlist wishlist = wishListDao.findById(String.valueOf(wishlistId)).orElseThrow();
        if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUserId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot modify this wishlist.");
        }
        Set<Product> productsToAdd = new HashSet<>(); // Use a Set to avoid duplicates
        for (String productId : productIds) {
            Product productToAdd = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
            productsToAdd.add(productToAdd);
        }
        for (Product product : productsToAdd) {
            if (!wishlist.getProducts().contains(product)) {
                wishlist.getProducts().add(product);
            } else {
                 throw new IllegalArgumentException("Product is already in this wishlist.");
            }
        }
        wishListDao.save(wishlist);
    }

    public void removeProductsFromWishlist(Set<String> productIds, Long wishlistId) throws IllegalAccessException, EntityNotFoundException, IllegalStateException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            throw new IllegalStateException("Logged user cannot be null");
        }
        Wishlist wishlist = wishListDao.findById(String.valueOf(wishlistId)).orElseThrow();
        if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUserId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot modify this wishlist.");
        }
        Set<Product> productsToRemove = new HashSet<>(); // Use a Set to avoid duplicates
        for (String productId : productIds) {
            Product productToRemove = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
            productsToRemove.add(productToRemove);
        }
        for (Product product : productsToRemove) {
            if (wishlist.getProducts().contains(product)) {
                wishlist.getProducts().remove(product);
            } else {
                 throw new IllegalArgumentException("Product is not in this wishlist.");
            }
        }
        wishListDao.save(wishlist);
    }

    public Wishlist mapToEntity(WishlistCreateDTO wishlistCreateDTO) { return modelMapper.map(wishlistCreateDTO, Wishlist.class);}
    public WishlistDTO mapToDto(Wishlist wishlist) { return modelMapper.map(wishlist, WishlistDTO.class); }
}











