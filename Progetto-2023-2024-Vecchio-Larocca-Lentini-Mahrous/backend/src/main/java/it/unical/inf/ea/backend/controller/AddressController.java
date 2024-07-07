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

import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/addresses", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class AddressController {

    private final AddressService addressService;

    @PostMapping(path= "/addAddress")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) {
        try {
            AddressDTO createdAddress = addressService.createAddress(addressCreateDTO);
            return ResponseEntity.ok(createdAddress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteAddress/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") UUID id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/setDefaultAddress/{id}")
    public ResponseEntity<?> setDefaultAddress(@PathVariable("id") UUID id) {
        try {
            addressService.setDefaultAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/updateAddress/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") UUID id, @Valid @RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllLoggedUserAddresses")
    public ResponseEntity<?> getAllLoggedUserAddresses() {
        try {
            return ResponseEntity.ok(addressService.getAllLoggedUserAddresses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAddress/{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllAddresses")
    public ResponseEntity<?> getAllAddresses() {
        try {
            return ResponseEntity.ok(addressService.getAllAddresses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
