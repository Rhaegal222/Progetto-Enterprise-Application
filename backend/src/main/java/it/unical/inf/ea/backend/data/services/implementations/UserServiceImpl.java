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
    public UserDTO createUser(UserDTO userDto) {
        if (userDao.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già esistente");
        }
        if (userDao.findByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username già esistente");
        }

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
        return modelMapper.map(savedUser, UserDTO.class);
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
}