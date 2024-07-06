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
import java.util.UUID;

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
    public ResponseEntity<?> addProductsToWishlist(@PathVariable String wishlistId, @PathVariable Long productId) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            wishlistService.addProductToWishlist(uuid,productId);
            return ResponseEntity.ok("{\"message\": \"Product added to wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteWishlist/{wishlistId}")
    public ResponseEntity<?> deleteWishlist(@PathVariable("wishlistId") String wishlistId) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            wishlistService.deleteWishlist(uuid);
            return ResponseEntity.ok("{\"message\": \"Wishlist deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }


    @DeleteMapping("/{wishlistId}/removeProductsFromWishlist/{productId}")
    public ResponseEntity<?> removeProductsFromWishlist(@PathVariable String wishlistId, @PathVariable Long productId) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            wishlistService.removeProductFromWishlist(uuid, productId);
            return ResponseEntity.ok("{\"message\": \"Product removed from wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getWishlistById/{wishlistId}")
    public ResponseEntity<?> getWishlistById(@PathVariable String wishlistId){
        try {
            UUID uuid = UUID.fromString(wishlistId);
            WishlistDTO wishlist = wishlistService.getWishlistById(uuid);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getProductByWishlistId/{wishlistId}")
    public ResponseEntity<?> getProductByWishlistId(@PathVariable  String wishlistId){
        try {
            UUID uuid = UUID.fromString(wishlistId);
            List<ProductDTO> product = wishlistService.getProductByWishlistId(uuid);
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
    @PutMapping("/updateWishlist/{wishlistId}")
    public ResponseEntity<?> updateWishlist(@PathVariable String wishlistId, @RequestBody WishlistDTO wishlistDTO) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            WishlistDTO updatedWishlist = wishlistService.updateWishlist(uuid, wishlistDTO);
            return ResponseEntity.ok(updatedWishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
    @PostMapping("/shareWishlist/{wishlistId}")
    public ResponseEntity<?> shareWishlist(@PathVariable String wishlistId, @RequestParam String email) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            wishlistService.shareWishlist(uuid, email);
            return ResponseEntity.ok("{\"message\": \"Wishlist shared successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
    @DeleteMapping("/removeWishlistAccess/{wishlistId}")
    public ResponseEntity<?> removeWishlistAccess(@PathVariable String wishlistId, @RequestParam String email) {
        try {
            UUID uuid = UUID.fromString(wishlistId);
            wishlistService.removeWishlistAccess(uuid, email);
            return ResponseEntity.ok("{\"message\": \"Wishlist access removed successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
