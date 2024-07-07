package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.UserImageService;
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
@RequestMapping(value = "/api/v1/profilePicture", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserImageController {
    private final UserImageService userImageService;

    @GetMapping(path = "/getPhotoProfileById/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getPhotoProfileById(@PathVariable("userId") String userId) throws IOException {
        try {
            Resource resource = userImageService.getPhotoProfileById(userId);
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value = "/uploadInitialPhotoProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadInitialPhotoProfile(@PathVariable("userId") UUID userId, @RequestPart("file") MultipartFile multipartFile) {
        try {
            userImageService.uploadInitialPhotoProfile(userId, multipartFile);
            return ResponseEntity.ok("{\"message\": \"Image uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/getMyPhotoProfile", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getMyPhotoProfile() {
        try {
            Resource resource = userImageService.getMyPhotoProfile();
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/replaceMyPhotoProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replaceMyPhotoProfile(@RequestParam("file") MultipartFile multipartFile){
        try {
            userImageService.replaceMyPhotoProfile(multipartFile);
            return ResponseEntity.ok("{\"message\": \"Image replaced successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/deleteMyPhotoProfile")
    public ResponseEntity<?> deleteMyPhotoProfile(){
        try {
            userImageService.deleteMyPhotoProfile();
            return ResponseEntity.ok("{\"message\": \"Image deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
