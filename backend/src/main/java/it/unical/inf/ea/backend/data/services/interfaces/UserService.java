package it.unical.inf.ea.backend.data.services.interfaces;

import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException;
    void deleteUser(String id);
    UserBasicDTO findUserById(String id);

    Optional<UserBasicDTO> findBasicByUsername(String username);

    Page<UserBasicDTO> searchUsersByUsername(String usernameQuery, int page, int size);

    Page<UserDTO> findAll(int page, int size, UserRole userRole, String username) throws IllegalAccessException;

    Optional<UserDTO> findByUsername(String username);

    Map<String, String> googleAuth(String code) throws Exception;


    Map<String, String> authenticateUser(String username, String password, Provider provider) throws JOSEException;

    ResponseEntity<String> registerUser(String firstname, String lastname, String email, String password) throws MessagingException;

    ResponseEntity<String> sendVerificationEmail(String username) throws MessagingException;

    Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException;

    void createUser(String lastname, String firstname, String email, String password) throws MessagingException;

    void activateUser(String token) throws ParseException, JOSEException;


    UserDTO changeRole(String userId, UserRole role);

    UserDTO banUser(String userId);

    UserDTO unBanUser(String userId);


    void logout(HttpServletRequest request) throws ParseException, JOSEException;

    void changePassword(String authorizationHeader, String oldPassword, String newPassword) throws ParseException, JOSEException;

    void getNewPasswordByEmail(String token) throws ParseException, JOSEException, MessagingException;

    void resetPassword(String email) throws MessagingException;

    UserDTO findMyProfile();

}
