package it.unical.inf.ea.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.UserImageService;
import it.unical.inf.ea.backend.dto.UserImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/images", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserImageController {
    private final UserImageService userImageService;

    @PostMapping(value = "/users/photo-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> savePhotoUser(
            @RequestPart("file") MultipartFile multipartFile,
            @RequestParam("description") String description
    ) {
        try {
            userImageService.savePhotoUser(multipartFile, description);
            return ResponseEntity.ok("{\"message\": \"Image uploaded successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(path = "/{type}/{folder_name}/{file_name:.*}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getImage(@PathVariable("type" )String type, @PathVariable("folder_name")String folder_name ,@PathVariable("file_name") String file_name) throws IOException {

        Resource resource = userImageService.getImage(type+"/"+folder_name+"/"+file_name);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/users/photo-profile/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhotoUser(@PathVariable("id") String id) throws IllegalAccessException {
        userImageService.deletePhotoUser(id);
    }


}
