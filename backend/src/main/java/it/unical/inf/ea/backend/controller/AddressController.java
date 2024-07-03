package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.AddressService;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/shipping-addresses", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class AddressController {

    private final AddressService addressService;

    @PostMapping(path="/addShippingAddress")
    public ResponseEntity<?> addShippingAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) {
        try {
            AddressDTO createdAddress = addressService.createAddress(addressCreateDTO);
            return ResponseEntity.ok(createdAddress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/setDefaultShippingAddress/{id}")
    public ResponseEntity<?> setDefaultShippingAddress(@PathVariable("id") String id) {
        try {
            addressService.setDefaultAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/updateShippingAddress/{id}")
    public ResponseEntity<?> updateShippingAddress(@PathVariable("id") String id, @Valid @RequestBody AddressDTO addressDTO) throws IllegalAccessException {
        try {
            AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteShippingAddress/{id}")
    public ResponseEntity<?> deleteShippingAddress(@PathVariable("id") String id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getShippingAddress/{id}")
    public ResponseEntity<?> getShippingAddress(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllShippingAddresses")
    public ResponseEntity<?> getAllShippingAddresses() {
        try {
            return ResponseEntity.ok(addressService.getAllAddresses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllLoggedUserShippingAddresses")
    public ResponseEntity<?> getAllLoggedUserShippingAddresses() {
        try {
            return ResponseEntity.ok(addressService.getAllLoggedUserShippingAddresses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
