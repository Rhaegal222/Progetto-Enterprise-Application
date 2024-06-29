package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.services.interfaces.BrandService;
import it.unical.inf.ea.backend.dto.BrandDTO;
import it.unical.inf.ea.backend.dto.creation.BrandCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/brand", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class BrandController {
    private final BrandService brandService;

    @PostMapping("/addBrand")
    public ResponseEntity<?> addBrand(@RequestBody BrandCreateDTO brandDTO) {
        try {
            brandService.addBrand(brandDTO);
            return ResponseEntity.ok("{\"message\": \"Brand registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/deleteBrand")
    public ResponseEntity<String> deleteBrand(@RequestParam Integer id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok("{\"message\": \"Brand deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/allBrands")
    public ResponseEntity<?> getAllBrands() {
        try {
            return ResponseEntity.ok(brandService.getAllBrands());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getBrandById")
    public ResponseEntity<?> getBrandById(@RequestParam Integer id) {
        try {
            Optional<Brand> brand = brandService.findBrandById(id);
            return ResponseEntity.ok(brand);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getBrandByName")
    public ResponseEntity<?> getBrandByName(@RequestParam String name) {
        try {
            Optional<Brand> brand = brandService.findBrandByName(name);
            return ResponseEntity.ok(brand);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
