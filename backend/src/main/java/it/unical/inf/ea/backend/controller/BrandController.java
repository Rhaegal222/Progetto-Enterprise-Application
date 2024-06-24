package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.services.interfaces.BrandService;
import it.unical.inf.ea.backend.dto.BrandDTO;
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addBrand(@RequestBody BrandDTO brandDTO) {
        try {
            brandService.addBrand(brandDTO);
            return ResponseEntity.ok("{\"message\": \"Brand registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @DeleteMapping("/deleteBrand")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteBrand(@RequestParam Integer id) {
        try {
            this.brandService.deleteBrand(id);
            return ResponseEntity.ok("{\"message\": \"Brand deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @GetMapping("/allBrands")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllBrands() {
        return ResponseEntity.ok(this.brandService.getAllBrands());
    }

    @GetMapping("/getBrandById/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBrandById(@RequestParam Integer id) {
        Optional<Brand> brand = brandService.findBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @GetMapping("/getBrandByName/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBrandByName(@RequestParam String name) {
        Optional<Brand> brand = brandService.findBrandByName(name);
        return ResponseEntity.ok(brand);
    }

}
