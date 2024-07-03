package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/wishlist", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/createWishlist")
    public ResponseEntity<?> createWishlist(@Valid @RequestBody WishlistCreateDTO wishListCreateDTO){
        try {
            wishlistService.createWishlist(wishListCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Wishlist created successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
    @GetMapping(path = "/getWishlistById/{id}")
    public ResponseEntity<?> getWishlistById(@PathVariable String id){
        try {
            WishlistDTO wishlist = wishlistService.getWishlistById(id);
            return ResponseEntity.ok(wishlist);
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


    @DeleteMapping(path = "/deleteWishlist/{id}")
    public ResponseEntity<?> deleteWishlist(@PathVariable("id") String id) {
        try {
            wishlistService.deleteWishlist(id);
            return ResponseEntity.ok("{\"message\": \"Wishlist deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/{wishlistId}/products")
    public ResponseEntity<?> addProductsToWishlist(@PathVariable String wishlistId, @RequestBody String productId) {
        try {
            wishlistService.addProductToWishlist(wishlistId, productId);
            return ResponseEntity.ok("{\"message\": \"Product added to wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{wishlistId}/products")
    public ResponseEntity<?> removeProductsFromWishlist(@PathVariable String wishlistId, @RequestBody String productId) {
        try {
            wishlistService.removeProductFromWishlist(productId, wishlistId);
            return ResponseEntity.ok("{\"message\": \"Product removed from wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }






}
