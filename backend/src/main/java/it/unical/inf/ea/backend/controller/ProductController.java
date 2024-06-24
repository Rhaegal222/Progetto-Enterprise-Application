package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.ProductCategory;
import it.unical.inf.ea.backend.data.services.interfaces.BrandService;
import it.unical.inf.ea.backend.data.services.interfaces.ProductCategoryService;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
    private final ProductCategoryService productCategoryService;
    private final BrandService brandService;

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

//    @GetMapping("/getAllProductsBrands")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<?> getAllProductsBrands() {
//        List<String> brands = productService.getAllProductsBrands();
//        return ResponseEntity.ok(brands);
//    }


    @PutMapping("/updateProduct/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductDTO> updateProduct(@RequestParam String id, @RequestBody ProductDTO product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    @GetMapping("/getProductsByCategory/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProductsByCategory(@RequestParam String categoryName) {
        ProductCategory productCategory = productCategoryService.findByCategoryName(categoryName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        List<ProductDTO> products = productService.getProductsByCategory(productCategory);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getProductsByBrand/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProductsByBrand(@RequestParam String brandName) {
        Brand brand = brandService.findBrandByName(brandName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        List<ProductDTO> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getProductById/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProductById(@RequestParam String id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/getProductsByPriceRange/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProductsByPriceRange(@RequestParam Double min, @RequestParam Double max) {
        List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

}
