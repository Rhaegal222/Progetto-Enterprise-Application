package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.OrderService;
import it.unical.inf.ea.backend.dto.OrderDTO;
import it.unical.inf.ea.backend.dto.OrderItemDTO;
import it.unical.inf.ea.backend.dto.creation.OrderCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/addOrder")
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        try {

            return ResponseEntity.ok(orderService.addOrder(orderCreateDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/updateOrder/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") UUID id, @Valid @RequestBody OrderItemDTO orderDTO) {
        try {

            return ResponseEntity.ok( orderService.updateOrder(id, orderDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(path = "/deleteOrder/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(orderService.deleteOrder(id));
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

    @GetMapping(path = "/getAllLoggedUserOrders")
    public ResponseEntity<?> getAllLoggedUserOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllLoggedUserOrders());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getOrderItems/{orderId}")
    public ResponseEntity<?> getOrderItems(@PathVariable("orderId") UUID orderId) {
        try {
            List<OrderItemDTO> orderItems = orderService.findAllOrderItemsByOrderId(orderId);
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Errore: " + e.getMessage() + "\"}");
        }
    }
}
