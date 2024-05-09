package it.unical.inf.ea.backend.data.services;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.dto.basics.OrderBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//import it.unical.inf.ea.backend.config.AppwriteConfig;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.User;

import it.unical.inf.ea.backend.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Slf4j

public class UserServicesImpl implements UserServices {

    private  ModelMapper modelMapper;
    //private final JwtContextUtils jwtContextUtils;

    private final UserDao userDao;
    private final ProductDao productDao;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDTO createUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user = userDao.save(user);

        return mapToDto(user);
    }
    // TO DO
    /*
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

        if(userDTO.getBio() != null && !userDTO.getBio().equals(oldUser.getBio()))
            oldUser.setBio(userDTO.getBio());

        userDao.save(oldUser);
        return mapToDto(oldUser);
    }

     */


    /*
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

            for(Product product: loggedUser.getSellingProducts()){
                if(product.getOrder()!=null && product.getOrder().isEmpty())
                    productDao.delete(product);
            }

            //TODO: delete all user's products
            userDao.save(loggedUser);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

     */

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

    public Optional<UserDTO> findByUsername(String username) {
        User user= userDao.findByUsername(username);
        if (user==null || user.getStatus().equals(UserStatus.BANNED) || user.getStatus().equals(UserStatus.CANCELLED) || user.getStatus().equals(UserStatus.HIDDEN))
            return Optional.empty();
        return Optional.of(mapToDto(user));
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
    public void createUser(String username, String password, String email) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setProvider(Provider.LOCAL);
        user.setReviewsTotalSum(0);
        user.setReviewsNumber(0);
        user.setEmailVerified(false);
        createUser(user);
    }
    //da cancellare
    @Override
    public Page<OrderBasicDTO> getMyOrders(int page, int size) {
        return null;
    }
    /*
    @Override
    public Page<OrderBasicDTO> getMyOrders(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userDao.findByUsername(username);
        Page<Order> orders = new PageImpl<Order>(user.getOrders(),PageRequest.of(page,size),user.getOrders().size());
        List<OrderBasicDTO> collect = orders.stream().map(s->modelMapper.map(s, OrderBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page,size), orders.getTotalElements());
    }

     */

    @Override
    public UserDTO banUser(String userId) {
        User user = userDao.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getRole().equals(UserRole.ADMIN)){
            throw new IllegalArgumentException("Admins cannot be banned");
        }
        user.setStatus(UserStatus.BANNED);
        return mapToDto(userDao.save(user));
    }

    public UserDTO mapToDto(User user){return modelMapper.map(user, UserDTO.class);}
    public UserBasicDTO mapToBasicDto(User user){return modelMapper.map(user,UserBasicDTO.class);}





}