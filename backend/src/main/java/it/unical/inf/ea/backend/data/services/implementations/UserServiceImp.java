package it.unical.inf.ea.backend.data.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.config.security.*;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.EmailService;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.OrderBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService{

    private final UserDao userDao;
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final ProductDao productDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;
    private final EmailService emailService;
    private final Oauth2GoogleValidation oauth2GoogleValidation;
    private final OAuth2KeycloakValidation oauth2KeycloakValidation;


    public UserDTO createUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user = userDao.save(user);

        return mapToDto(user);
    }

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
    @Transactional
    public void  deleteUser(String id) {
        try{
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(id))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Request");

            loggedUser.setStatus(UserStatus.CANCELLED);
            String newUsername = "deletedUser_" + loggedUser.getId();
            loggedUser.setUsername(newUsername);

            userDao.save(loggedUser);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public UserBasicDTO findUserById(String id) {
        User user = userDao.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!user.getStatus().equals(UserStatus.ACTIVE)){
            throw new EntityNotFoundException();

        }
        return mapToBasicDto(user);
    }

    public Optional<UserDTO> findByUsername(String username) {
        User user= userDao.findByUsername(username);
        if (user==null || user.getStatus().equals(UserStatus.BANNED) || user.getStatus().equals(UserStatus.CANCELLED) || user.getStatus().equals(UserStatus.HIDDEN))
            return Optional.empty();
        return Optional.of(mapToDto(user));
    }

    @Override
    public Optional<UserBasicDTO> findBasicByUsername(String username) {
        User user= userDao.findByUsername(username);
        if (user==null)
            return Optional.empty();
        return Optional.of(mapToBasicDto(user));
    }

    @Override
    public Page<UserBasicDTO> searchUsersByUsername(String usernameQuery, int page, int size) {
        Page<User> allByUsernameContainingIgnoreCase = userDao.findAllByUsernameContainingIgnoreCase(PageRequest.of(page, size), usernameQuery);
        return allByUsernameContainingIgnoreCase.map(this::mapToBasicDto);
    }

    public Page<UserDTO> findAll(int page, int size, UserRole userRole, String username) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!loggedUser.getRole().equals(UserRole.ADMIN))
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


    private Pair<Boolean,UserDTO> processOAuthPostGoogleLogin(String username, String email) {

        User existUser = userDao.findByEmail(email);

        if (existUser == null) {
            User newUser = new User();
            findBasicByUsername(username).ifPresentOrElse(
                    user -> newUser.setUsername(username + "_" + UUID.randomUUID().toString().substring(0, 7)),
                    () -> newUser.setUsername(username));
            newUser.setUsername(username);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setPassword(passwordEncoder.encode(Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD));
            newUser.setEmail(email);
            newUser.setRole(UserRole.USER);
            newUser.setEmailVerified(true);
            return new Pair<>(false, createUser(newUser));
        }
        return new Pair<>(true, mapToDto(existUser));
    }

    private Pair<Boolean,UserDTO> processOAuthPostKeycloakLogin(String username, String email) {
        User existUser = userDao.findByEmail(email);

        if (existUser == null) {
            User newUser = new User();
            findBasicByUsername(username).ifPresentOrElse(
                    user -> newUser.setUsername(username + "_" + UUID.randomUUID().toString().substring(0, 7)),
                    () -> newUser.setUsername(username));
            newUser.setUsername(username);
            newUser.setProvider(Provider.KEYCLOAK);
            newUser.setPassword(passwordEncoder.encode(Constants.STANDARD_KEYCLOAK_ACCOUNT_PASSWORD));
            newUser.setEmail(email);
            newUser.setRole(UserRole.USER);
            newUser.setEmailVerified(true);
            return new Pair<>(false, createUser(newUser));
        }
        return new Pair<>(true, mapToDto(existUser));
    }
    @Override
    public Map<String, String> googleAuth(String code) throws Exception {
        try {

            Map<String, String> userInfo = oauth2GoogleValidation.validate(code);
            Pair<Boolean, UserDTO> pair = processOAuthPostGoogleLogin(userInfo.get("name"), userInfo.get("email"));

            if(!pair.getUserExists()) {
                UserImage userImage = new UserImage();
                userImage.setUrlPhoto(userInfo.get("pictureUrl"));
                userImage.setUser(mapToEntity(pair.getUser()));
            }

            return authenticateUser(pair.getUser().getUsername(), Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD, Provider.GOOGLE);
        }
        catch (Exception e) {
            log.error("Error validating google code: " + e.getMessage(), e);
            throw new Exception("Error validating google code", e);
        }
    }

    @Override
    public Map<String, String> keycloakAuth(String code) {
        try {

            Map<String, String> userInfo = oauth2KeycloakValidation.validate(code);
            Pair<Boolean, UserDTO> pair = processOAuthPostKeycloakLogin(userInfo.get("name"), userInfo.get("email"));

            if(!pair.getUserExists()) {
                UserImage userImage = new UserImage();
                userImage.setUrlPhoto(Constants.STANDARD_USER_ACCOUNT_PHOTO_KEYCLOAK);
                userImage.setUser(mapToEntity(pair.getUser()));
            }

            return authenticateUser(pair.getUser().getUsername(), Constants.STANDARD_KEYCLOAK_ACCOUNT_PASSWORD, Provider.KEYCLOAK);
        }
        catch (Exception e) {
            log.error("Error validating keycloak token: " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error validating keycloak token", e);
        }
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
    public ResponseEntity<String> registerUser(String username, String email, String password) {
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
        return new ResponseEntity<>( "user created" , HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> sendVerificationEmail(String username) throws MessagingException {
        User user = userDao.findByUsername(username);
        if(user == null)
            return new ResponseEntity<>( "user not found" , HttpStatus.NOT_FOUND);
        if(user.isEmailVerified())
            return new ResponseEntity<>( "user already verified" , HttpStatus.CONFLICT);
        String token = tokenStore.createEmailToken(username, Constants.EMAIL_VERIFICATION_CLAIM);
        String url = Constants.BASE_PATH + "users/activate?token=" + token;
        emailService.sendEmail(user.getEmail(), Constants.VERIFICATION_EMAIL_SUBJECT,Constants.VERIFICATION_EMAIL_TEXT + url);
        return new ResponseEntity<>( "verification email sent" , HttpStatus.OK);
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
    @Transactional
    public void changePassword(String oldPassword, String newPassword, HttpServletRequest request) throws ParseException, JOSEException, MessagingException {
        if (newPassword.length() < 8)
            throw new RuntimeException("Password must be at least 8 characters long");
        if (oldPassword.equals(newPassword))
            throw new RuntimeException("New password must be different from old password");
        User user = jwtContextUtils.getUserLoggedFromContext();
        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new RuntimeException("Wrong old password");
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader("refresh-token");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            String refreshToken2 = refreshToken.substring("Bearer ".length());
            tokenStore.logout(accessToken, refreshToken2);
        }else
            throw new RuntimeException("Token is missing");
    }

    @Override
    @Transactional
    public void changePassword(String token) throws ParseException, JOSEException, MessagingException {
        tokenStore.verifyToken(token, Constants.RESET_PASSWORD_CLAIM);
        String username = tokenStore.getUser(token);
        User user = userDao.findByUsername(username);
        if(user == null)
            throw new RuntimeException("User not found");
        String newPassword = RandomStringUtils.randomAlphanumeric(12);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
        emailService.sendEmail(user.getEmail(), Constants.NEW_PASSWORD_EMAIL_SUBJECT,Constants.NEW_PASSWORD_EMAIL_TEXT + newPassword);
    }

    @Override
    public void resetPassword(String email) throws MessagingException {
        User user = userDao.findByEmail(email);
        if(user == null)
            throw new RuntimeException("User not found");
        String token = tokenStore.createEmailToken(user.getUsername(), Constants.RESET_PASSWORD_CLAIM);
        String url = Constants.BASE_PATH + "users/getNewPassword?token=" + token;
        emailService.sendEmail(user.getEmail(), Constants.RESET_PASSWORD_EMAIL_SUBJECT,Constants.RESET_PASSWORD_EMAIL_TEXT + url);
    }

    @Override
    public UserDTO findMyProfile() {
        try {
            User user = jwtContextUtils.getUserLoggedFromContext();
            if (user == null)
                return null;
            UserDTO userDTO = modelMapper.map(user,UserDTO.class);
            return userDTO;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public void throwOnIdMismatch(String id, UserDTO userDTO){
        if(userDTO.getId() != null && !userDTO.getId().equals(id))
            throw new IdMismatchException();
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
    public UserDTO changeRole(String userId, UserRole role) {
        User user = userDao.findById(userId).orElseThrow(EntityNotFoundException::new);
        if (jwtContextUtils.getUserLoggedFromContext().getId().equals(userId))
            throw new IllegalArgumentException("You cannot change your own role");
        user.setRole(role);
        return mapToDto(userDao.save(user));
    }

    @Override
    public UserDTO banUser(String userId) {
        User user = userDao.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getRole().equals(UserRole.ADMIN)){
            throw new IllegalArgumentException("Admins cannot be banned");
        }
        user.setStatus(UserStatus.BANNED);
        return mapToDto(userDao.save(user));
    }

    @Override
    public UserDTO unBanUser(String userId) {
        User user = userDao.findById(userId).orElseThrow(EntityNotFoundException::new);

        user.setStatus(UserStatus.ACTIVE);
        return mapToDto(userDao.save(user));
    }

    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
    public UserDTO mapToDto(User user){return modelMapper.map(user, UserDTO.class);}
    public UserBasicDTO mapToBasicDto(User user){return modelMapper.map(user,UserBasicDTO.class);}

    class Pair<T, U> {
        private T userExists;
        private U user;

        public Pair(T userExists, U user) {
            this.userExists = userExists;
            this.user = user;
        }

        public Pair() {
        }

        public T getUserExists() {
            return userExists;
        }

        public U getUser() {
            return user;
        }

    }

}
