package it.unical.inf.ea.backend.Controller;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Product-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping("/AddProduct")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO product) {
        ResponseEntity<String> response = validateProduct(product);
        if(response!=null) return response;
        try {
            productService.createProduct(product);
            return ResponseEntity.ok("{\"message\": \"Product registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @DeleteMapping("/deleteProduct/")
    public ResponseEntity<String> deleteProduct(@RequestParam Long id) {
        try {
            productService.deleteProduct(productService.getProductById(id));
            return ResponseEntity.ok("{\"message\": \"User deleted successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/getProductById/")
    public ResponseEntity<?> getProductById(@RequestParam Long id) {
        User user = productService.getProductById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/updateProduct/")
    public ResponseEntity<?> updateUser(@RequestParam Long id, @RequestBody ProductDTO product) {
        ResponseEntity<?> response = validateProduct(product);
        if (response != null) return response;
        try {
            Product productToUpdate =productService.getProductById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            productToUpdate.setTitle(product.getTitle());
            productToUpdate.setDescription(product.getDescription());
            productToUpdate.setProductCost(product.getProductCost());
            productService.createProduct(product);
            return ResponseEntity.ok("{\"message\": \"User updated successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\": \"Error: " + e + "\"");
        }
    }


    private ResponseEntity<String> validateProduct(ProductDTO product) {
        if (product.getTitle() == null || product.getTitle().isEmpty()) return ResponseEntity.badRequest().body("{\"message\": \"Name cannot be empty\"}");
        if (product.getProductCategory() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Category cannot be null\"}");
        if (product.getProductCost() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Cost cannot be null\"}");
        if (product.getDeliveryCost() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Delivery Cost cannot be null\"}");
        if (product.getProductSize() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Product Size cannot be null\"}");
        return null;
    }
}
