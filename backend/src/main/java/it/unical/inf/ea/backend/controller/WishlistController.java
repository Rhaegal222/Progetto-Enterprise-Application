package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/wishlist", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/addWishlist")
    public ResponseEntity<?> addWishlist(@Valid @RequestBody WishlistCreateDTO wishListCreateDTO){
        try {
            wishlistService.addWishlist(wishListCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Wishlist created successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/{wishlistId}/addProductsToWishlist/{productId}")
    public ResponseEntity<?> addProductsToWishlist(@PathVariable Long wishlistId, @PathVariable Long productId) {
        try {
            wishlistService.addProductToWishlist(wishlistId,productId );
            return ResponseEntity.ok("{\"message\": \"Product added to wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteWishlist/{wishlistId}")
    public ResponseEntity<?> deleteWishlist(@PathVariable("wishlistId") Long wishlistId) {
        try {
            wishlistService.deleteWishlist(wishlistId);
            return ResponseEntity.ok("{\"message\": \"Wishlist deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{wishlistId}/removeProductsFromWishlist/{productId}")
    public ResponseEntity<?> removeProductsFromWishlist(@PathVariable Long wishlistId, @PathVariable Long productId) {
        try {
            wishlistService.removeProductFromWishlist(wishlistId, productId);
            return ResponseEntity.ok("{\"message\": \"Product removed from wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getWishlistById/{wishlistId}")
    public ResponseEntity<?> getWishlistById(@PathVariable Long wishlistId){
        try {
            WishlistDTO wishlist = wishlistService.getWishlistById(wishlistId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getProductByWishlistId/{wishlistId}")
    public ResponseEntity<?> getProductByWishlistId(@PathVariable Long wishlistId){
        try {
            List<ProductDTO> product = wishlistService.getProductByWishlistId(wishlistId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getAllLoggedUserWishlists")
    public ResponseEntity<?> getAllLoggedUserWishlists(){
        try {
            return ResponseEntity.ok(wishlistService.getAllLoggedUserWishlists());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
