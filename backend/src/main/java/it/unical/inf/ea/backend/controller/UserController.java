package it.unical.inf.ea.backend.controller;

import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.config.security.LoginWithGoogleBody;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/users", produces = "application/json")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/authenticate")
    public ResponseEntity<Map<String, String>> authenticate(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(userService.authenticateUser(username, password, Provider.LOCAL));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            userService.registerUser(firstname, lastname, email, password);
            userService.sendVerificationEmail(email);
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String unique_code) {
        try {
            userService.activateUser(unique_code);
            return ResponseEntity.ok().build();
        } catch (ParseException | JOSEException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<?> partialUpdateUser(@PathVariable("id") String id, @RequestBody Map<String, Object> updates) {
        try {
            userService.partialUpdateUser(id, updates);
            return ResponseEntity.ok("{\"message\": \"User updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("{\"message\": \"User deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/findUserById/{id}")
    public ResponseEntity<?> findUserById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(userService.findUserById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<?> findByUsername(@RequestParam("username") String username) {
        try {
            Optional<UserBasicDTO> userBasicDTO = userService.findBasicByUsername(username);
            return userBasicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/searchByUsername")
    public ResponseEntity<?> searchByUsername(@RequestParam("username") String username, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            return ResponseEntity.ok(userService.searchUsersByUsername(username, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/googleAuthentication")
    public ResponseEntity<Map<String, String>> googleAuth(@RequestParam String idTokenString) {
        try {
            return ResponseEntity.ok(userService.googleAuth(idTokenString));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            return ResponseEntity.ok(userService.getUserBasicDTO());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/retrieveUserProfile")
    public ResponseEntity<?> retrieveUserProfile() {
        try {
            return ResponseEntity.ok(userService.getUserDTO());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(userService.refreshToken(request.getHeader(AUTHORIZATION), response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/rejectToken")
    public ResponseEntity<?> rejectToken(HttpServletRequest request) {
        try {
            userService.rejectToken(request);
            return ResponseEntity.ok("{\"message\": \"Token rejected successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            userService.logout(request);
            return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        try {
            userService.resetPassword(email);
            return ResponseEntity.ok("{\"message\": \"Password reset email sent successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        try {
            userService.changePassword(request.getHeader(AUTHORIZATION), oldPassword, newPassword);
            return ResponseEntity.ok("{\"message\": \"Password changed successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getNewPassword")
    public ResponseEntity<?> getNewPassword(@RequestParam("token") String token) {
        try {
            userService.getNewPasswordByEmail(token);
            return ResponseEntity.ok("{\"message\": \"New password email sent successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}
