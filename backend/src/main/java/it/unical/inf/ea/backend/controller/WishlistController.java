package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import it.unical.inf.ea.backend.data.services.interfaces.WishlistService;
import it.unical.inf.ea.backend.dto.WishListDTO;
import it.unical.inf.ea.backend.dto.creation.WishListCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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
    public ResponseEntity<WishListDTO> createWishlist(@RequestBody WishListCreateDTO wishListCreateDTO){
        try {
            return ResponseEntity.ok(wishlistService.createWishlist(wishListCreateDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    




}
