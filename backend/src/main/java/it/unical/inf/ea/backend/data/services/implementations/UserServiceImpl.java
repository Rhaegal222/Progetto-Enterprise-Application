package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.data.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserDao userDao, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid user data", e);
        }
    }

    @Override
    public void addUser(UserDTO userDto) {
        // validateUserBeforeSave(userDto);

        User user = new User();
        user.setId(userDto.getId());
        user.setRole(userDto.getRole());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setProvider(userDto.getProvider());
        user.setEmailVerified(userDto.isEmailVerified());
        user.setStatus(userDto.getStatus());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userDao.save(user);
        modelMapper.map(savedUser, UserDTO.class);
    }



    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }


    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userDao.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public Optional<User> findById(String id) {
        return userDao.findById(id);
    }



    @Override
    public void deleteUser(String id) {
        User userToDelete = userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userDao.delete(userToDelete);
    }

    private void validateUserBeforeSave(UserDTO userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userDto.isEmailVerified()) {
            throw new IllegalArgumentException("Email verification is required");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userDto.getProvider() == null) {
            throw new IllegalArgumentException("Provider is required");
        }
        if (userDto.getStatus() == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (userDto.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}