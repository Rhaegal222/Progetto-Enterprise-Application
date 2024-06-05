package it.unical.inf.ea.backend.Controller;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/User-api/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    @Qualifier("userService")
    private final UserService UserService;

    private final UserDao userDao;

    private final UserService userService;

    private PasswordEncoder passwordEncoder;


    @PostMapping("/AddUser")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        ResponseEntity<String> response = validateUser(userDTO);
        if(response!=null) return response;
        try {
            userService.createUser(userDTO);
            return ResponseEntity.ok("{\"message\": \"User registered successfully\"}");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Email already in use\"}");
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Invalid data\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e + "\"}");
        }
    }

    @DeleteMapping("/deleteUser/")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        try {
            userService.deleteUser(userService.getUserById(String.valueOf(id)).getId());
            return ResponseEntity.ok("{\"message\": \"User deleted successfully\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("\"message\":" + e + "\""); // JSON response
        }
    }

    @PutMapping("/updateUser/")
    public ResponseEntity<?> updateUser(@RequestParam Long id, @RequestBody UserDTO userDTO) {
        ResponseEntity<String> response = validateUser(userDTO);
        if (response != null) return response;
        try {
            User userToUpdate = userService.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            userToUpdate.setUsername(userDTO.getSurname());
            userToUpdate.setEmail(userDTO.getEmail());
            userToUpdate.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userService.createUser(userDTO);
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

    @GetMapping("/getUserById/")
    public ResponseEntity<?> getUserById(@RequestParam Long id) {
        User user = userService.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(user);
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

    private ResponseEntity<String> validateUser(UserDTO UserDTO) {
        if (UserDTO.getSurname() == null || UserDTO.getSurname().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Surname is required\"}"); // JSON response
        }
        if (UserDTO.getName() == null || UserDTO.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"First name is required\"}"); // JSON response
        }
        if (UserDTO.getEmail() == null || UserDTO.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Email is required\"}"); // JSON response
        }
        if (UserDTO.getPassword() == null || UserDTO.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Password is required\"}"); // JSON response
        }
        return null;
    }
}
