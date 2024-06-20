package it.unical.inf.ea.backend.data.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.config.security.Constants;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.services.interfaces.EmailService;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtContextUtils jwtContextUtils;
    private final TokenStore tokenStore;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public UserDTO createUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user = userDao.save(user);

        return mapToDto(user);
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User oldUser = userDao.findById(id).orElseThrow();

        if(!id.equals(userDTO.getId()))
            throw new IllegalAccessException("User cannot change another user");

        if(!id.equals(loggedUser.getId()) && (!loggedUser.isAdministrator()) )
            throw new IllegalAccessException("User cannot change another user");


        if(!oldUser.getUsername().equals(userDTO.getUsername()) && userDao.findByUsername(userDTO.getUsername()) != null)
            throw new IllegalAccessException("Username already exists");
        if(!oldUser.getEmail().equals(userDTO.getEmail()) && userDao.findByEmail(userDTO.getEmail()) != null)
            throw new IllegalAccessException("Email already exists");

        oldUser.setUsername(userDTO.getUsername());
        oldUser.setEmail(userDTO.getEmail());

        userDao.save(oldUser);
        return mapToDto(oldUser);
    }

    @Override
    public void deleteUser(String id) {
        User userToDelete = userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userDao.delete(userToDelete);
    }

    @Override
    public void registerUser(String username, String email, String password) {
        if (username == null || email == null || password == null)
            throw new IllegalArgumentException("username, email and password cannot be null");
        if (password.length() < 8)
            throw new IllegalArgumentException("password must be at least 8 characters long");

        if(findByUsername(username).isPresent())
            throw new IllegalArgumentException("username already exists");
        if(userDao.findByEmail(email) != null)
            throw new IllegalArgumentException("email already exists");
        createUser(username, passwordEncoder.encode(password), email);
        log.info("User created: " + username);
        new ResponseEntity<>("user created", HttpStatus.CREATED);
    }

    @Override
    public void sendVerificationEmail(String username) throws MessagingException {
        User user = userDao.findByUsername(username);
        if(user == null) {
            new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
            return;
        }
        if(user.isEmailVerified()) {
            new ResponseEntity<>("user already verified", HttpStatus.CONFLICT);
            return;
        }
        String token = tokenStore.createEmailToken(username, Constants.EMAIL_VERIFICATION_CLAIM);
        String url = Constants.BASE_PATH + "users/activate?token=" + token;
        emailService.sendEmail(user.getEmail(), Constants.VERIFICATION_EMAIL_SUBJECT,Constants.VERIFICATION_EMAIL_TEXT + url);
        new ResponseEntity<>("verification email sent", HttpStatus.OK);
    }

    @Override
    public Map<String, String> authenticateUser(String username, String password, Provider provider) throws JOSEException {
        User u = userDao.findByUsername(username);
        if(!provider.equals(Provider.GOOGLE) && password.equals(Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD)
                && u.getProvider().equals(Provider.GOOGLE))
            throw new IllegalArgumentException("You cannot login with password with a google linked account");

        if(!provider.equals(Provider.KEYCLOAK) && password.equals(Constants.STANDARD_KEYCLOAK_ACCOUNT_PASSWORD)
                && u.getProvider().equals(Provider.KEYCLOAK))
            throw new IllegalArgumentException("You cannot login with password with a keycloak linked account");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String accessToken = tokenStore.createAccessToken(Map.of("username", username, "role", u.getAuthorities().toString()));
        String refreshToken = tokenStore.createRefreshToken(username);
        return Map.of("accessToken", "Bearer "+accessToken, "refreshToken", "Bearer "+refreshToken);
    }


    @Override
    public Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                String username = tokenStore.getUser(refreshToken);
                UserDTO user = findByUsername(username).orElseThrow(()->new RuntimeException("user not found"));

                User userDetails = mapToEntity(user);
                String accessToken = tokenStore.createAccessToken(Map.of("username", userDetails.getUsername(), "role", userDetails.getAuthorities().toString()));

                return Map.of("accessToken", "Bearer "+accessToken, "refreshToken", "Bearer "+refreshToken);
            }
            catch (Exception e) {
                log.error(String.format("Error refresh token: %s", authorizationHeader), e);
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
        return null;
    }

    @Override
    public void logout(HttpServletRequest request) throws ParseException, JOSEException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader("refresh-token");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            String refreshToken2 = refreshToken.substring("Bearer ".length());
            tokenStore.logout(accessToken, refreshToken2);
        } else {
            throw new RuntimeException("Token is missing");
        }
    }

    @Override
    public void createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setProvider(Provider.LOCAL);
        user.setEmailVerified(false);
        createUser(user);
    }

    @Override
    public void activateUser(String token) throws ParseException, JOSEException {
        tokenStore.verifyToken(token, Constants.EMAIL_VERIFICATION_CLAIM);
        String username = tokenStore.getUser(token);

        User user = userDao.findByUsername(username);
        user.setEmailVerified(true);
        userDao.save(user);

        new ResponseEntity<>("user activated", HttpStatus.OK);
    }

    @Override
    public Page<UserDTO> findAll(int page, int size, UserRole userRole, String username) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!loggedUser.getRole().equals(UserRole.ADMIN) && !loggedUser.getRole().equals(UserRole.ADMIN))
            throw new IllegalAccessException("You can't access this list");

        if(username==null)
            username="";

        if(userRole.equals(UserRole.USER))
            return userDao.findAllByRoleEqualsAndUsernameContains(PageRequest.of(page, size ),userRole,username)
                    .map(this::mapToDto);
        else
            return userDao.findAllByRoleEqualsOrRoleEquals(PageRequest.of(page, size ),UserRole.ADMIN,UserRole.ADMIN)
                    .map(this::mapToDto);
    }

    public Optional<UserDTO> findByUsername(String username) {
        User user= userDao.findByUsername(username);
        if (user==null || user.getStatus().equals(UserStatus.BANNED) || user.getStatus().equals(UserStatus.CANCELLED) || user.getStatus().equals(UserStatus.HIDDEN))
            return Optional.empty();
        return Optional.of(mapToDto(user));
    }

    @Override
    public UserBasicDTO findUserById(String id) {
        User user = userDao.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!user.getStatus().equals(UserStatus.ACTIVE)){
            if(user.getStatus().equals(UserStatus.HOLIDAY))
                return mapToBasicDto(user);
            throw new EntityNotFoundException();

        }
        return mapToBasicDto(user);
    }

    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid user data", e);
        }
    }



    public UserDTO mapToDto(User user){return modelMapper.map(user, UserDTO.class);}
    public UserBasicDTO mapToBasicDto(User user){return modelMapper.map(user, UserBasicDTO.class);}
    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
}