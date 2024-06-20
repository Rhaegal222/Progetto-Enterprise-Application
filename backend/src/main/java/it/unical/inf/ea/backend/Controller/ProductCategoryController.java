package it.unical.inf.ea.backend.controller;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.data.services.interfaces.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private final ProductDao productDao;

    @PostMapping("/addCategory")
    public ResponseEntity<?> addCategory(@RequestBody ProductCategoryDTO productCategory) {
        try {
            productCategoryService.addCategory(productCategory);
            return ResponseEntity.ok("{\"message\": \"productCategory registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<String> deleteCategory(@RequestParam String id) {
        try {
            this.productCategoryService.deleteCategory(id);
            return ResponseEntity.ok("{\"message\": \"Category deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(this.productCategoryService.getAllCategories());
    }
}
