package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.AddressService;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> addShippingAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) throws IllegalAccessException {
        try {
            addressService.createAddress(addressCreateDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/setDefaultShippingAddress/{id}")
    public ResponseEntity<Void> setDefaultShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        try {
            addressService.setDefaultAddress(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/updateShippingAddress/{id}")
    public ResponseEntity<AddressDTO> updateShippingAddress(@PathVariable("id") String id, @Valid @RequestBody AddressDTO addressDTO) throws IllegalAccessException {
        try {
            return ResponseEntity.ok(addressService.updateAddress(id, addressDTO));
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(path = "/deleteShippingAddress/{id}")
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/getShippingAddress/{id}")
    public ResponseEntity<AddressDTO> getShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/getAllShippingAddresses")
    public ResponseEntity<?> getAllShippingAddresses() {
        try {
            return ResponseEntity.ok(addressService.getAllAddresses());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
