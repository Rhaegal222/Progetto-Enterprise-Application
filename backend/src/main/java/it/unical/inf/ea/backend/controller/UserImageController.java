package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.UserImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/profilePicture", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserImageController {
    private final UserImageService userImageService;

    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestPart("file") MultipartFile multipartFile, @RequestParam("description") String description) {
        try {
            userImageService.uploadImage(multipartFile, description);
            return ResponseEntity.ok("{\"message\": \"Image uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "getImage/{type}/{folder_name}/{file_name:.*}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getImage(@PathVariable("type" )String type, @PathVariable("folder_name")String folder_name ,@PathVariable("file_name") String file_name) {
        try {
            Resource resource = userImageService.getImage(type+"/"+folder_name+"/"+file_name);
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/deleteImage/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable("id") String id){
        try {
            userImageService.deleteImage(id);
            return ResponseEntity.ok("{\"message\": \"Image deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
