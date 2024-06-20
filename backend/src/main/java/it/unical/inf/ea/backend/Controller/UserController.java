package it.unical.inf.ea.backend.controller;

import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticate(@RequestParam( "username" ) String username, @RequestParam( "password" ) String password, HttpServletResponse response) throws JOSEException {
        return ResponseEntity.ok(userService.authenticateUser(username, password, Provider.LOCAL));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            userService.sendVerificationEmail(user.getUsername());
            return ResponseEntity.ok("{\"message\": \"User registered successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }
}
