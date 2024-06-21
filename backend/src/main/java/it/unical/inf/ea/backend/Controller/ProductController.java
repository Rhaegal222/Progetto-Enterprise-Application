package it.unical.inf.ea.backend.controller;

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

@RestController
@RequestMapping("/product-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductDao productDao;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            productService.addProduct(productCreateDTO);
            return ResponseEntity.ok("{\"message\": \"Product registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @DeleteMapping("/deleteProduct/")
    public ResponseEntity<String> deleteProduct(@RequestParam String id) {
        try {
            productService.deleteProduct(productService.getProductById(id).getId());
            return ResponseEntity.ok("{\"message\": \"Product deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/getProductById/")
    public ResponseEntity<?> getProductById(@RequestParam String id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/updateProduct/")
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
