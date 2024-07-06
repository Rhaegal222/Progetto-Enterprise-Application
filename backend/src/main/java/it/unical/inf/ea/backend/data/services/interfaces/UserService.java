package it.unical.inf.ea.backend.data.services.interfaces;

import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO partialUpdateUser(UUID id, Map<String, Object> updates) throws IllegalAccessException;

    UserBasicDTO updateMe(Map<String, Object> updates) throws IllegalAccessException;

    void deleteUser(UUID id);

    UserBasicDTO findUserById(UUID id);

    Optional<UserBasicDTO> findBasicByUsername(String username);

    Page<UserDTO> findAll(int page, int size, UserRole userRole, String username) throws IllegalAccessException;

    Optional<UserDTO> findByUsername(String username);

    List<UserDTO> getAllUser() throws IllegalAccessException;

    Map<String, String> googleAuth(String code) throws Exception;


    Map<String, String> authenticateUser(String username, String password, Provider provider) throws JOSEException;

    ResponseEntity<String> registerUser(String firstname, String lastname, String email, String password) throws MessagingException;

    ResponseEntity<String> sendVerificationEmail(String username) throws MessagingException;

    void rejectToken(HttpServletRequest request) throws ParseException;

    Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException, ParseException, JOSEException;

    void createUser(String lastname, String firstname, String email, String password) throws MessagingException;

    void activateUser(String token) throws ParseException, JOSEException;


    UserDTO changeRole(UUID userId, UserRole role) throws IllegalAccessException;

    UserDTO banUser(UUID userId);

    UserDTO unBanUser(UUID userId);


    void logout(HttpServletRequest request) throws ParseException, JOSEException;

    void changePassword(String authorizationHeader, String oldPassword, String newPassword) throws ParseException, JOSEException;

    void getNewPasswordByEmail(String token) throws ParseException, JOSEException, MessagingException;

    void resetPassword(String email) throws MessagingException;

    UserDTO getUserDTO() throws EntityNotFoundException;

    UserBasicDTO getUserBasicDTO() throws EntityNotFoundException;


}
