package it.unical.inf.ea.backend.data.services.interfaces;

import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@Service
public interface UserService {
    UserDTO createUser(User user);
    UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException;
    void deleteUser(String id);


    void registerUser(String username, String email, String password);
    void sendVerificationEmail(String username) throws MessagingException;
    void activateUser(String token) throws ParseException, JOSEException;
    Map<String, String> authenticateUser(String username, String password, Provider provider) throws JOSEException;
    void logout(HttpServletRequest request) throws ParseException, JOSEException;
    Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException;

    void createUser(String username, String password, String email);

    Page<UserDTO> findAll(int page, int size, UserRole userRole, String username) throws IllegalAccessException;
    Optional<UserDTO> findByUsername(String username);
    UserBasicDTO findUserById(String id);

    void save(User user);
}