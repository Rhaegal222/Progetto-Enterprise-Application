package it.unical.inf.ea.backend.data.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import it.unical.inf.ea.backend.config.security.*;
import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.EmailService;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static it.unical.inf.ea.backend.config.FileUploadUtil.saveBufferedImage;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService{

    private final UserDao userDao;
    // private final ProductService productService;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final CartDao cartDao;
    // private final ProductDao productDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;
    private final EmailService emailService;
    private final Oauth2GoogleValidation oauth2GoogleValidation;

    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";

    public UserDTO createUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user = userDao.save(user);
        Cart cart = new Cart();
        cart.setUser(user);
        cartDao.save(cart);
        return mapToDto(user);
    }

    @Override
    public UserDTO partialUpdateUser(UUID id, Map<String, Object> updates) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User user = userDao.findById(id).orElseThrow();

        if (!id.equals(loggedUser.getId()) && !loggedUser.isAdministrator()) {
            throw new IllegalAccessException("User cannot change another user");
        }

        // Verifica se l'username è già esistente
        if (updates.containsKey("username")) {
            String newUsername = (String) updates.get("username");
            if (!user.getEmail().equals(newUsername) && userDao.findByEmail(newUsername) != null) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        // Verifica se l'email è già esistente
        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (!user.getEmail().equals(newEmail) && userDao.findByEmail(newEmail) != null) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        // Applica gli aggiornamenti alle proprietà dell'utente
        BeanWrapper beanWrapper = new BeanWrapperImpl(user);
        updates.forEach((key, value) -> {
            if (beanWrapper.isWritableProperty(key)) {
                beanWrapper.setPropertyValue(key, value);
            }
        });

        // Salva l'utente aggiornato
        User updatedUser = userDao.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public void  deleteUser(UUID id) {
        try{
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if(loggedUser.getRole().equals(UserRole.USER))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Request");

            User user = userDao.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setStatus(UserStatus.CANCELLED);
            String newUsername = "deletedUser_" + user.getId();
            user.setUsername(newUsername);

            userDao.save(user);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting user", e);
        }
    }

    @Override
    public UserBasicDTO findUserById(UUID id) {
        User user = userDao.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!user.getStatus().equals(UserStatus.ACTIVE)){
            throw new EntityNotFoundException();

        }
        return mapToBasicDto(user);
    }

    public Optional<UserDTO> findByUsername(String username) {
        User user= userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if (user==null || !user.getStatus().equals(UserStatus.ACTIVE))
            return Optional.empty();
        return Optional.of(mapToDto(user));
    }

    @Override
    public Optional<UserBasicDTO> findBasicByUsername(String username) {
        User user= userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if (user==null)
            return Optional.empty();
        return Optional.of(mapToBasicDto(user));
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

    @Override
    public List<UserDTO> getAllUser() throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(!loggedUser.getRole().equals(UserRole.ADMIN))
            throw new IllegalAccessException("You can't access this list");

        List<User> users = userDao.findAll();
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Pair<Boolean,UserDTO> processOAuthPostGoogleLogin(Map<String, String> userInfo) {

        String email = userInfo.get("email");
        User existUser = userDao.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        if (existUser == null) {
            if (userDao.findByUsername(email).isPresent())
                throw new IllegalArgumentException("Username already exists");
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setLastName(userInfo.get("familyName"));
            newUser.setFirstName(userInfo.get("givenName"));
            newUser.setProvider(Provider.GOOGLE);
            newUser.setPassword(passwordEncoder.encode(Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD));
            newUser.setEmail(email);
            newUser.setRole(UserRole.USER);
            newUser.setEmailVerified(true);
            return new Pair<>(false, createUser(newUser));
        }
        return new Pair<>(true, mapToDto(existUser));
    }

    private void retrieveProfileImage(String url, String userId) throws IOException, IllegalAccessException {

        URL imageUrl = new URL(url);
        BufferedImage image = ImageIO.read(imageUrl);

        saveBufferedImage(userDir + userId, "photoProfile.png", image);
    }

    @Override
    public Map<String, String> googleAuth(String code) throws Exception {
        try {
            Map<String, String> userInfo = oauth2GoogleValidation.validate(code);
            Pair<Boolean, UserDTO> pair = processOAuthPostGoogleLogin(userInfo);

            retrieveProfileImage(userInfo.get("pictureUrl"), pair.getUser().getId());

            return authenticateUser(pair.getUser().getUsername(), Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD, Provider.GOOGLE);
        }
        catch (Exception e) {
            log.error("Error validating google code: {}", e.getMessage(), e);
            throw new Exception("Error validating google code", e);
        }
    }

    @Override
    public Map<String, String> authenticateUser(String username, String password, Provider provider) throws JOSEException {

        User u = userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);

        if(!provider.equals(Provider.GOOGLE) && password.equals(Constants.STANDARD_GOOGLE_ACCOUNT_PASSWORD)
                && u.getProvider().equals(Provider.GOOGLE))
            throw new IllegalArgumentException("You cannot login with password with a google linked account");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String accessToken = tokenStore.createAccessToken(Map.of("username", username, "role", u.getAuthorities().toString()));
        String refreshToken = tokenStore.createRefreshToken(username);
        return Map.of("accessToken", "Bearer "+accessToken, "refreshToken", "Bearer "+refreshToken);
    }

    @Override
    public ResponseEntity<String> registerUser(String firstname, String lastname, String email, String password) {
        if (firstname == null || lastname == null || email == null || password == null)
            throw new IllegalArgumentException("firstname, lastname, email and password cannot be null");
        if (password.length() < 8)
            throw new IllegalArgumentException("password must be at least 8 characters long");

        if(findByUsername(email).isPresent())
            throw new IllegalArgumentException("username already exists");
        if(userDao.findByEmail(email) != null)
            throw new IllegalArgumentException("email already exists");
        createUser(lastname, firstname, email, passwordEncoder.encode(password));
        log.info("User created: {}", firstname);
        return new ResponseEntity<>( "user created" , HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> sendVerificationEmail(String username) throws MessagingException {
        User user = userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
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
    public void rejectToken(HttpServletRequest request) throws ParseException {
        String token = tokenStore.getToken(request);
        tokenStore.rejectToken(token);
    }

    @Override
    public Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                String username = tokenStore.getUser(refreshToken);
                UserDTO user = findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

                User userDetails = mapToEntity(user);
                String accessToken = tokenStore.createAccessToken(Map.of("username", userDetails.getUsername(), "role", userDetails.getAuthorities().toString()));

                return Map.of("accessToken", "Bearer "+accessToken, "refreshToken", "Bearer "+refreshToken);
            } catch (Exception e) {
                log.error(String.format("Error refresh token: %s", authorizationHeader));
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
        processRequest(request);
    }

    @Override
    @Transactional
    public void changePassword(String authorizationHeader, String oldPassword, String newPassword) throws ParseException, JOSEException {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String accessToken = authorizationHeader.substring("Bearer ".length());
            tokenStore.verifyToken(accessToken, Constants.RESET_PASSWORD_CLAIM);
            String username = tokenStore.getUser(accessToken);
            User user = userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
            if(user == null)
                throw new RuntimeException("User not found");
            if (newPassword.length() < 8)
                throw new RuntimeException("Password must be at least 8 characters long");
            if (oldPassword.equals(newPassword))
                throw new RuntimeException("New password must be different from old password");
            if(!passwordEncoder.matches(oldPassword, user.getPassword()))
                throw new RuntimeException("Wrong old password");
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.save(user);
        }
    }

    @Override
    public UserDTO changeRole(UUID userId, UserRole role) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(!loggedUser.getRole().equals(UserRole.ADMIN))
            throw new IllegalAccessException("Only admin can change role");

        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role);
        return mapToDto(userDao.save(user));
    }

    @Override
    @Transactional
    public void getNewPasswordByEmail(String token) throws ParseException, JOSEException, MessagingException {
        tokenStore.verifyToken(token, Constants.RESET_PASSWORD_CLAIM);
        String username = tokenStore.getUser(token);
        User user = userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if(user == null)
            throw new RuntimeException("User not found");
        String newPassword = RandomStringUtils.randomAlphanumeric(12);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
        emailService.sendEmail(user.getEmail(), Constants.NEW_PASSWORD_EMAIL_SUBJECT,Constants.NEW_PASSWORD_EMAIL_TEXT + newPassword);
    }

    private void processRequest(HttpServletRequest request) throws ParseException, JOSEException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader("refresh-token");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            String refreshToken2 = refreshToken.substring("Bearer ".length());
            tokenStore.logout(accessToken, refreshToken2);
        } else throw new RuntimeException("Token is missing");
    }

    @Override
    public void resetPassword(String email) throws MessagingException {
        User user = userDao.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        if(user == null)
            throw new RuntimeException("User not found");
        String token = tokenStore.createEmailToken(user.getUsername(), Constants.RESET_PASSWORD_CLAIM);
        String url = Constants.BASE_PATH + "users/getNewPassword?token=" + token;
        emailService.sendEmail(user.getEmail(), Constants.RESET_PASSWORD_EMAIL_SUBJECT,Constants.RESET_PASSWORD_EMAIL_TEXT + url);
    }

    @Override
    public UserDTO getUserDTO() {
        User user = jwtContextUtils.getUserLoggedFromContext();
        if (user == null || !user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to access the profile");
        }
        return mapToDto(user);
    }

    @Override
    public UserBasicDTO getUserBasicDTO() {
        User user = jwtContextUtils.getUserLoggedFromContext();
        if (user == null || !user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to access the profile");
        }
        return modelMapper.map(user, UserBasicDTO.class);
    }

    public void throwOnIdMismatch(String id, UserDTO userDTO){
        if(userDTO.getId() != null && !userDTO.getId().equals(id))
            throw new IdMismatchException("L'id dell'ordine non corrisponde");
    }

    @Override
    public void createUser(String lastname, String firstname, String email, String password) {
        try {
            User user = new User();
            user.setUsername(email);
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(UserRole.USER);
            user.setProvider(Provider.LOCAL);
            user.setEmailVerified(false);
            createUser(user);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating user", e);
        }
    }

    @Override
    public void activateUser(String token) throws ParseException, JOSEException {
        tokenStore.verifyToken(token, Constants.EMAIL_VERIFICATION_CLAIM);
        String username = tokenStore.getUser(token);

        User user = userDao.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        user.setEmailVerified(true);
        userDao.save(user);

        new ResponseEntity<>("user activated", HttpStatus.OK);
    }


    @Override
    public UserDTO banUser(UUID userId) {
        User user = userDao.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getRole().equals(UserRole.ADMIN)){
            throw new IllegalArgumentException("Admins cannot be banned");
        }
        user.setStatus(UserStatus.BANNED);
        return mapToDto(userDao.save(user));
    }

    @Override
    public UserDTO unBanUser(UUID userId) {
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

        public Pair() { }

        public T getUserExists() {
            return userExists;
        }

        public U getUser() {
            return user;
        }

    }

}
