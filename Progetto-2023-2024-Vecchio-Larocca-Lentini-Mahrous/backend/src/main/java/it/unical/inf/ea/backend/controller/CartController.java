package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.CartService;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartItemCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/cart", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class CartController {

    private final CartService cartService;

    @GetMapping("/getCartByUserId/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable UUID userId) {
        try {
            CartDTO cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getCartForLoggedUser")
    public ResponseEntity<?> getCartForLoggedUser() {
        try {
            CartDTO cart = cartService.getCartForLoggedUser();
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/editItem")
    public ResponseEntity<?> editItemInCart(@RequestParam UUID cartItemId, @RequestParam int quantity) {
        try {
            CartDTO cart = cartService.editItemInCart(cartItemId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemCreateDTO cartItemDTO) {
        try {
            CartDTO cart = cartService.addItemToCart(cartItemDTO);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/removeItem/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable UUID cartItemId) {
        try {
            CartDTO cart = cartService.removeItemFromCart(cartItemId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<?> clearCart() {
        try {

            return ResponseEntity.ok(cartService.clearCart());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
