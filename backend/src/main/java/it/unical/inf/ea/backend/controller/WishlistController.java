package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishlistDTO;
import it.unical.inf.ea.backend.dto.creation.WishlistCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Set;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/wishList", produces="application/json")
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
    public ResponseEntity<?> getWishlistById(@PathVariable Long id){
        try {
            WishlistDTO wishlist = wishlistService.getWishlistById(id);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getAllWishlists")
    public ResponseEntity<?> getAllWishlists(){
        try {
            return ResponseEntity.ok(wishlistService.getAllWishlists());
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
    public ResponseEntity<?> addProductsToWishlist(@PathVariable String wishlistId, @RequestBody Set<String> productIds) {
        try {
            wishlistService.addProductsToWishlist(productIds, wishlistId);
            return ResponseEntity.ok("{\"message\": \"Products added to wishlist successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }






}