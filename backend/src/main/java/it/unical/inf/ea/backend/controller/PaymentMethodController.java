package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.PaymentMethodService;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/payment-methods", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping(path="/addPaymentMethod")
    public ResponseEntity<?> addPaymentMethod(@Valid @RequestBody PaymentMethodCreateDTO paymentMethodCreateDTO) {
        try {
            paymentMethodService.createPaymentMethod(paymentMethodCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Payment method registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/setDefaultPaymentMethod/{id}")
    public ResponseEntity<?> setDefaultPaymentMethod(@PathVariable("id") UUID id) {
        try {
            paymentMethodService.setDefaultPaymentMethod(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/updatePaymentMethod/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable("id") UUID id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        try {
            paymentMethodService.updatePaymentMethod(id, paymentMethodDTO);
            return ResponseEntity.ok("{\"message\": \"Payment method updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deletePaymentMethod/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable("id") UUID id) {
        try {
            paymentMethodService.deletePaymentMethod(id);
            return ResponseEntity.ok("{\"message\": \"Payment method deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getPaymentMethod/{id}")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable("id") UUID id) {
        try {
            PaymentMethodDTO paymentMethod = paymentMethodService.getPaymentMethodById(id);
            return ResponseEntity.ok(paymentMethod);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllPaymentMethods")
    public ResponseEntity<?> getAllPaymentMethods() {
        try {
            return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllLoggedUserPaymentMethods")
    public ResponseEntity<?> getAllLoggedUserPaymentMethods() {
        try {
            return ResponseEntity.ok(paymentMethodService.getAllLoggedUserPaymentMethods());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
