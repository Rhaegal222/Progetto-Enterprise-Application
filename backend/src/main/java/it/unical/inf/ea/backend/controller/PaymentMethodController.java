package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.services.interfaces.PaymentMethodService;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.basics.PaymentMethodBasicDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(@Valid @RequestBody PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(paymentMethodCreateDTO));
    }

    @PutMapping(path = "/setDefaultPaymentMethod/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaymentMethodDTO> setDefaultPaymentMethod(@PathVariable("id") String id) throws IllegalAccessException {
        paymentMethodService.setDefaultPaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/updatePaymentMethod/{id}")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable("id") String id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, paymentMethodDTO));
    }

    @DeleteMapping(path = "/deletePaymentMethod/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable("id") String id) throws IllegalAccessException {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/getPaymentMethod/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaymentMethodDTO> getPaymentMethod(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }

    @GetMapping(path = "/getAllPaymentMethods")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

}