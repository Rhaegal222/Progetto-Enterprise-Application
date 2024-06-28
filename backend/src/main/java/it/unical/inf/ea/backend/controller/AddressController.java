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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AddressDTO> addShippingAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) throws IllegalAccessException {
        return ResponseEntity.ok(addressService.createAddress(addressCreateDTO));
    }

    @PutMapping(path = "/setDefaultShippingAddress/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> setDefaultShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        addressService.setDefaultAddress(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/updateShippingAddress/{id}")
    public ResponseEntity<AddressDTO> updateShippingAddress(@PathVariable("id") String id, @Valid @RequestBody AddressDTO addressDTO) throws IllegalAccessException {
        return ResponseEntity.ok(addressService.updateAddress(id, addressDTO));
    }

    @DeleteMapping(path = "/deleteShippingAddress/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/getShippingAddress/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AddressDTO> getShippingAddress(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @GetMapping(path = "/getAllShippingAddresses")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllShippingAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

}
