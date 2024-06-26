package it.unical.inf.ea.backend.controller;


import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.config.security.LoginWithGoogleBody;
import it.unical.inf.ea.backend.config.security.Oauth2GoogleValidation;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

import static it.unical.inf.ea.backend.config.security.AppSecurityConfig.SECURITY_CONFIG_NAME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Oauth2GoogleValidation oauth2GoogleValidation;

    @PostMapping(path = "/authenticate" )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, String>> authenticate( @RequestParam( "username" ) String username, @RequestParam( "password" ) String password, HttpServletResponse
            response) throws JOSEException {
        System.out.println(username);
        System.out.println(password);
        return ResponseEntity.ok(userService.authenticateUser(username, password, Provider.LOCAL));
    }

    @PostMapping(path= "/register" )
    @ResponseStatus(HttpStatus.OK)
    public void register(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("password" ) String password) throws MessagingException {
        userService.registerUser(firstname, lastname, email, password);
        userService.sendVerificationEmail(email);
    }

    @GetMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@RequestParam("token") String unique_code) throws ParseException, JOSEException {
        userService.activateUser(unique_code);
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(userService.updateUser(id,patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBasicDTO> userById(@PathVariable("id") String id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/find-by-username")
    public ResponseEntity<UserBasicDTO> findByUsername(@RequestParam("username") String username){
        Optional<UserBasicDTO> userBasicDTO = userService.findBasicByUsername(username);
        return userBasicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }

    @GetMapping("/search-by-username")
    public ResponseEntity<Page<UserBasicDTO>> searchByUsername(@RequestParam("username") String username, @RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(userService.searchUsersByUsername(username, page, size));
    }


    @PostMapping("/google-auth")
    public ResponseEntity<Map<String, String>> googleAuth(@RequestParam String idTokenString) throws Exception {
        return ResponseEntity.ok(userService.googleAuth(idTokenString));
    }

    @PostMapping(value = "/login-with-google", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public ResponseEntity<Map<String, String>> loginWithGoogle(LoginWithGoogleBody body) throws Exception {
        return ResponseEntity.ok(userService.googleAuth(body.getCredential()));
    }

    @PostMapping("/keycloak-auth")
    public ResponseEntity<Map<String, String>> keycloakAuth(@RequestParam String idTokenString) throws Exception {
        return ResponseEntity.ok(userService.keycloakAuth(idTokenString));
    }

    @PostMapping(value = "/login-with-keycloak", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public ResponseEntity<Map<String, String>> loginWithKeycloak(String body) throws Exception {
        return ResponseEntity.ok(userService.keycloakAuth(body));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() throws EntityNotFoundException {
        return ResponseEntity.ok(userService.findMyProfile());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String >> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return ResponseEntity.ok(userService.refreshToken(request.getHeader(AUTHORIZATION), response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, JOSEException {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam("email") String email) throws EntityNotFoundException, MessagingException {
        userService.resetPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) throws EntityNotFoundException, ParseException, JOSEException {
        userService.changePassword(request.getHeader(AUTHORIZATION), oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getNewPassword")
    public ResponseEntity<Void> getNewPassword(@RequestParam("token") String token) throws EntityNotFoundException, ParseException, JOSEException, MessagingException {
        userService.getNewPasswordByEmail(token);
        return ResponseEntity.ok().build();
    }
}
