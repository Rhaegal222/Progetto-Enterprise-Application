package it.unical.inf.ea.backend.controller;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private PasswordEncoder passwordEncoder;


    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody UserDTO user) {
        try {
            userService.addUser(user);
            return ResponseEntity.ok("{\"message\": \"User registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }


    @DeleteMapping("/deleteUser/")
    public ResponseEntity<String> deleteUser(@RequestParam String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("{\"message\": \"User deleted successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @PutMapping("/updateUser/")
    public ResponseEntity<?> updateUser(@RequestParam String id, @RequestBody UserDTO user) {
        try {
            User userToUpdate = userService.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            userService.save(userToUpdate);
            return ResponseEntity.ok("{\"message\": \"User updated successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\": \"Error: " + e + "\"");
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }



    @GetMapping("/getUserByEmail/")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }
}
