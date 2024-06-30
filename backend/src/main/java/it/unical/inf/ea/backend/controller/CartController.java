package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.CartService;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.creation.CartCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable String userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<CartDTO> addProductToCart(@RequestBody CartCreateDTO cartCreateDTO) {
        CartDTO updatedCart = cartService.addProductToCart(cartCreateDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/removeProduct/{cartItemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable String cartItemId) {
        cartService.removeProductFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateProduct/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItem(@PathVariable String cartItemId, @RequestParam int quantity) {
        CartDTO updatedCart = cartService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }
}
