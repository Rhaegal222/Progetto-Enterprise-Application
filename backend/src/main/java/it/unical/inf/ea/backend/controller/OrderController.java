package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.OrderService;
import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/orders", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path="/addOrder")
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            orderService.addOrder(orderCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Ordine registrato con successo\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/updateOrder/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") UUID id, @Valid @RequestBody OrderDTO orderDTO) {
        try {
            orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok("{\"message\": \"Ordine aggiornato con successo\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteOrder/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") UUID id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("{\"message\": \"Ordine cancellato con successo\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getOrder/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") UUID id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }
}
