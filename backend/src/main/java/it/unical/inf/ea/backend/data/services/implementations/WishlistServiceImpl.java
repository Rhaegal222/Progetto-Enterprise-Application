package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.WishlistDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Wishlist;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistDao wishlistDao;
    private final ModelMapper modelMapper;
    private final ProductDao productDao;
    private final JwtContextUtils jwtContextUtils;
    private final Clock clock;


    @Override
    public void addWishlist(WishlistCreateDTO wishlistCreateDTO) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }
            LocalDateTime now = getTimeNow();

            Wishlist wishlist = mapToEntity(wishlistCreateDTO);
            wishlist.setWishlistName(wishlistCreateDTO.getWishlistName());
            wishlist.setVisibility(wishlistCreateDTO.getVisibility());
            wishlist.setCreatedAt(now);
            wishlist.setUser(loggedUser);

            wishlistDao.save(wishlist);

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
        List<Wishlist> wishlists = wishlistDao.findAllByUserId(loggedUser.getId());
        return wishlists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public WishlistDTO getWishlistById(UUID wishlistId) {
        Wishlist wishlist = wishlistDao.findById(wishlistId);
        if (wishlist == null) {
            throw new EntityNotFoundException("Wishlist not found");
        }
        return modelMapper.map(wishlist, WishlistDTO.class);
    }

    @Override
    public List<ProductDTO> getProductByWishlistId(UUID wishlistId) throws IllegalAccessException {
        Wishlist wishlist = wishlistDao.findById(wishlistId);
        if (wishlist == null) {
            throw new EntityNotFoundException("Wishlist not found");
        }        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUser().equals(loggedUser))
            throw new IllegalAccessException("User cannot get products from this wishlist");
        return wishlist.getProducts().stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    @Override
    public WishlistDTO updateWishlist(UUID wishlistId, WishlistDTO wishlistDTO) {
        try {
            Wishlist wishlist = wishlistDao.findById(wishlistId);
            if (wishlist == null) {
                throw new EntityNotFoundException("Wishlist not found");
            }
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUser().equals(loggedUser))
                throw new IllegalAccessException("User cannot update wishlist");
            wishlist.setWishlistName(wishlistDTO.getWishlistName());
            wishlist.setVisibility(wishlistDTO.getVisibility());
            return mapToDto(wishlistDao.save(wishlist));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteWishlist(UUID wishlistId) {
        try {
            Wishlist wishlist = wishlistDao.findById(wishlistId);
            if (wishlist == null) {
                throw new EntityNotFoundException("Wishlist not found");
            }
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser.getRole().equals(UserRole.USER) && !wishlist.getUser().equals(loggedUser))
                throw new IllegalAccessException("User cannot delete wishlist");

            wishlistDao.deleteById(wishlistId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void addProductToWishlist(UUID wishlistId, Long productId) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Wishlist wishlist = wishlistDao.findById(wishlistId);
            if (wishlist == null) {
                throw new EntityNotFoundException("Wishlist not found");
            }

            Product product = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));

            if (!wishlist.getProducts().contains(product)) {
                wishlist.getProducts().add(product);
            } else {
                throw new IllegalArgumentException("Product is already in this wishlist.");
            }

            mapToDto(wishlistDao.save(wishlist));
        } catch (Exception e) {
            throw new RuntimeException("Cannot add product to wishlist");
        }
    }

    @Override
    public void removeProductFromWishlist(UUID wishlistId, Long productId) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Product product = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));

            Wishlist wishlist = wishlistDao.findById(wishlistId);
            if (wishlist == null) {
                throw new EntityNotFoundException("Wishlist not found");
            }

            if (wishlist.getProducts().contains(product)) {
                wishlist.getProducts().remove(product);
            } else {
                throw new IllegalArgumentException("Product is not in this wishlist.");
            }

            mapToDto(wishlistDao.save(wishlist));
        } catch (Exception e) {
            throw new RuntimeException("Cannot remove product from wishlist");
        }
    }
    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }

    public Wishlist mapToEntity(WishlistCreateDTO wishlistCreateDTO) { return modelMapper.map(wishlistCreateDTO, Wishlist.class);}
    public WishlistDTO mapToDto(Wishlist wishlist) { return modelMapper.map(wishlist, WishlistDTO.class); }
}











