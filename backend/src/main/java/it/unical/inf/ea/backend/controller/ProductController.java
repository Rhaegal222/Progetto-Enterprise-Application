package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.BrandService;
import it.unical.inf.ea.backend.data.services.interfaces.CategoryService;
import it.unical.inf.ea.backend.data.services.interfaces.ProductImageService;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductImageService productImageService;

    @PostMapping("/addProduct")
    public ResponseEntity<Map<String,String>> addProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            Map<String, String> response = productService.addProduct(productCreateDTO);

            Resource resource = new ClassPathResource("images/product_placeholder.png");
            InputStream inputStream = resource.getInputStream();
            byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
            MultipartFile multipartFile = new MockMultipartFile("file", "product_placeholder.png", "image/png", imageBytes);

            Product product = productDao.findByName(productCreateDTO.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            productImageService.uploadInitialPhotoProductById(product.getId(), multipartFile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            productImageService.deletePhotoProductById(id);
            return ResponseEntity.ok("{\"message\": \"Product deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @PatchMapping(path = "/updateProduct/{id}")
    public ResponseEntity<?> partialUpdateUser(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
        try {
            productService.partialUpdateProduct(id, updates);
            return ResponseEntity.ok("{\"message\": \"Product updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) throws IllegalAccessException {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(productService.getAllProducts());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/getProductsByCategory/")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String categoryName) {
        try {
            Category category = categoryService.findByCategoryName(categoryName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            List<ProductDTO> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @GetMapping("/getProductsByBrand/")
    public ResponseEntity<?> getProductsByBrand(@RequestParam String brandName) {
        try {
            Brand brand = brandService.findBrandByName(brandName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
            List<ProductDTO> products = productService.getProductsByBrand(brand);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @GetMapping("/getProductsByPriceRange/")
    public ResponseEntity<?> getProductsByPriceRange(@RequestParam Double min, @RequestParam Double max) {
        try {
            List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @GetMapping("/getSalesProducts")
    public ResponseEntity<?> getSalesProducts() {
        try {
            List<ProductDTO> products = productService.getSalesProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }
}
