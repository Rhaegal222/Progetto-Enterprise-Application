package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.ProductImageService;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/productPicture", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping(value = "/uploadInitialPhotoProductById/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadInitialPhotoProductById(@PathVariable("productId") Long productId, @RequestPart("file") MultipartFile multipartFile) throws IOException, IllegalAccessException {
        try {
            productImageService.uploadInitialPhotoProductById(productId, multipartFile);
            return ResponseEntity.ok("{\"message\": \"Image uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }    }

    @GetMapping(path = "/getPhotoProductById/{productId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getPhotoProductById(@PathVariable("productId") Long productId) {
        try {
            Resource resource = productImageService.getPhotoProductById(productId);
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @PutMapping(value = "/replacePhotoProductById/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replacePhotoProductById(@PathVariable("productId") Long productId, @RequestParam("file") MultipartFile multipartFile){
        try {
            productImageService.replacePhotoProductById(productId, multipartFile);
            return ResponseEntity.ok("{\"message\": \"Image replaced successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/deletePhotoProductById/{productId}")
    public ResponseEntity<?> deletePhotoProductById(@PathVariable("productId") Long productId){
        try {
            productImageService.deletePhotoProductById(productId);
            return ResponseEntity.ok("{\"message\": \"Image deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
