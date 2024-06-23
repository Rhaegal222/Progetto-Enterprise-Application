package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/products", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)

public class ProductController {

    private final ProductService productService;
    private final ProductDao productDao;

    @PostMapping("/addProduct")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            productService.addProduct(productCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Product registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @DeleteMapping("/deleteProduct/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteProduct(@RequestParam String id) {
        try {
            productService.deleteProduct(productService.getProductById(id).getId());
            return ResponseEntity.ok("{\"message\": \"Product deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @GetMapping("/getAllProducts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/getProductById/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProductById(@RequestParam String id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/updateProduct/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateProduct(@RequestParam String id, @RequestBody ProductDTO product) {
        try {
            ProductDTO productToUpdate = productDao.findProductById(String.valueOf(id));
            productToUpdate.setTitle(product.getTitle());
            productToUpdate.setDescription(product.getDescription());
            productService.save(productToUpdate);
            return ResponseEntity.ok("{\"message\": \"Product updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\": \"Error: " + e + "\"");
        }
    }
}
