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
    public ResponseEntity<UserDTO> partialUpdateUser(@PathVariable("id") String id, @RequestBody Map<String, Object> updates) {
        try {
            return ResponseEntity.ok(userService.partialUpdateUser(id, updates));
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/findUserById/{id}")
    public ResponseEntity<UserBasicDTO> findUserById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(userService.findUserById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<UserBasicDTO> findByUsername(@RequestParam("username") String username) {
        try {
            Optional<UserBasicDTO> userBasicDTO = userService.findBasicByUsername(username);
            return userBasicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/searchByUsername")
    public ResponseEntity<Page<UserBasicDTO>> searchByUsername(@RequestParam("username") String username, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            return ResponseEntity.ok(userService.searchUsersByUsername(username, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/google-auth")
    public ResponseEntity<Map<String, String>> googleAuth(@RequestParam String idTokenString) {
        try {
            return ResponseEntity.ok(userService.googleAuth(idTokenString));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/login-with-google", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public ResponseEntity<Map<String, String>> loginWithGoogle(LoginWithGoogleBody body) {
        try {
            return ResponseEntity.ok(userService.googleAuth(body.getCredential()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserBasicDTO> me() {
        try {
            return ResponseEntity.ok(userService.getUserBasicDTO());
        } catch (EntityNotFoundException e) {
            // NOT AUTHORIZED
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/retrieveUserProfile")
    public ResponseEntity<UserDTO> retrieveUserProfile() {
        try {
            return ResponseEntity.ok(userService.getUserDTO());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(userService.refreshToken(request.getHeader(AUTHORIZATION), response));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/rejectToken")
    public ResponseEntity<Void> rejectToken(HttpServletRequest request) {
        try {
            userService.rejectToken(request);
            return ResponseEntity.ok().build();
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            userService.logout(request);
            return ResponseEntity.ok().build();
        } catch (ParseException | JOSEException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam("email") String email) {
        try {
            userService.resetPassword(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | MessagingException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        try {
            userService.changePassword(request.getHeader(AUTHORIZATION), oldPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | ParseException | JOSEException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/getNewPassword")
    public ResponseEntity<Void> getNewPassword(@RequestParam("token") String token) {
        try {
            userService.getNewPasswordByEmail(token);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | ParseException | JOSEException | MessagingException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
