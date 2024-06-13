package it.unical.inf.ea.backend.Controller;
import it.unical.inf.ea.backend.data.dao.ProductDao;
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
    private final ProductDao productDao;

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
            productService.deleteProduct(productService.getProductById(id).getId());
            return ResponseEntity.ok("{\"message\": \"Product deleted successfully\"}");
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
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/updateProduct/")
    public ResponseEntity<?> updateProduct(@RequestParam Long id, @RequestBody ProductDTO product) {
        ResponseEntity<?> response = validateProduct(product);
        if (response != null) return response;
        try {
            ProductDTO productToUpdate = productDao.findProductById(String.valueOf(id));
            productToUpdate.setTitle(product.getTitle());
            productToUpdate.setDescription(product.getDescription());
            productService.createProduct(product);
            return ResponseEntity.ok("{\"message\": \"Product updated successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\": \"Error: " + e + "\"");
        }
    }


    private ResponseEntity<String> validateProduct(ProductDTO product) {
        if (product.getTitle() == null || product.getTitle().isEmpty()) return ResponseEntity.badRequest().body("{\"message\": \"Name cannot be empty\"}");
        if (product.getProductCategory() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Category cannot be null\"}");
        if (product.getProductSize() == null ) return ResponseEntity.badRequest().body("{\"message\": \"Product Size cannot be null\"}");
        return null;
    }
}
