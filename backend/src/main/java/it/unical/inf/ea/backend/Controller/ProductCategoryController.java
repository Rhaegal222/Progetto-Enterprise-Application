package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.data.services.interfaces.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/productCategory", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)

public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping("/addCategory")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addCategory(@RequestBody ProductCategoryDTO productCategory) {
        try {
            productCategoryService.addCategory(productCategory);
            return ResponseEntity.ok("{\"message\": \"productCategory registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @DeleteMapping("/deleteCategory")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCategory(@RequestParam String id) {
        try {
            this.productCategoryService.deleteCategory(id);
            return ResponseEntity.ok("{\"message\": \"Category deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @GetMapping("/allCategories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(this.productCategoryService.getAllCategories());
    }
}
