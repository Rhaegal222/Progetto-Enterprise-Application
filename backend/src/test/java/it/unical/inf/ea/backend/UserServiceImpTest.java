package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.implementations.UserServiceImp;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @InjectMocks
    private UserServiceImp userService;

    @Mock
    private UserDao userDao;

    @Mock
    private CartDao cartDao;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtContextUtils jwtContextUtils;

    @Mock
    private TokenStore tokenStore;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;


    private User user;
    private UserDTO userDTO;

    private UserBasicDTO userBasicDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setProvider(Provider.LOCAL);
        user.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setId(String.valueOf(user.getId()));
        userDTO.setUsername("testuser");
        userDTO.setEmail("testuser@example.com");

    }

    @Test
    void createUser() {
        when(userDao.save(any(User.class))).thenReturn(user);
        when(cartDao.save(any(Cart.class))).thenReturn(new Cart());
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO createdUserDTO = userService.createUser(user);

        assertThat(createdUserDTO).usingRecursiveComparison().isEqualTo(userDTO);
        verify(userDao, times(1)).save(user);
        verify(cartDao, times(1)).save(any(Cart.class));
    }

    @Test
    void partialUpdateUser() throws IllegalAccessException {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", "updateduser");

        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(user);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO updatedUserDTO = userService.partialUpdateUser(user.getId(), updates);

        assertThat(updatedUserDTO).usingRecursiveComparison().isEqualTo(userDTO);
        verify(userDao, times(1)).save(user);
    }

    @Test
    void findUserById() {
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserBasicDTO.class)).thenReturn(userBasicDTO);

        UserBasicDTO foundUserBasicDTO = userService.findUserById(user.getId());

        assertThat(foundUserBasicDTO).usingRecursiveComparison().isEqualTo(userBasicDTO);
        verify(userDao, times(1)).findById(user.getId());
    }

    @Test
    void findByUsername() {
        when(userDao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        Optional<UserDTO> foundUserDTO = userService.findByUsername(user.getUsername());

        assertThat(foundUserDTO).isPresent();
        assertThat(foundUserDTO.get()).usingRecursiveComparison().isEqualTo(userDTO);
        verify(userDao, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void authenticateUser() throws Exception {
        when(userDao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(tokenStore.createAccessToken(anyMap())).thenReturn("accessToken");
        when(tokenStore.createRefreshToken(user.getUsername())).thenReturn("refreshToken");

        Map<String, String> tokens = userService.authenticateUser(user.getUsername(), "password", Provider.LOCAL);

        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isEqualTo("Bearer accessToken");
        assertThat(tokens.get("refreshToken")).isEqualTo("Bearer refreshToken");
    }

    @Test
    void changePassword() throws Exception {
        String newPassword = "NewPassword123!";

        when(tokenStore.getUser(anyString())).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        userService.changePassword("Bearer token", "password", newPassword);

        verify(userDao, times(1)).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

}
