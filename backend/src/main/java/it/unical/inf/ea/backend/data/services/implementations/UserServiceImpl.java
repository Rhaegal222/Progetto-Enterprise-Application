package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.UserDao; // Replace with your user repository interface
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.data.entities.User; // Replace with your user entity
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // If applicable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDto) {
        // Convert UserDto to User entity
        User user = modelMapper.map(userDto, User.class);

        // Additional logic for password encoding (if applicable)
        if (User.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(User.getPassword()));
        }

        // Save user in database
        user = userDao.save(user);

        // Convert User entity back to UserDto
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userDao.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userDao.findById(id).orElse(null);
        return user != null ? modelMapper.map(user, UserDTO.class) : null;
    }

    @Override
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDto) {
        User existingUser = userDao.findById(id).orElse(null);

        if (existingUser != null) {
            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());

            existingUser = userDao.save(existingUser);

            return modelMapper.map(existingUser, UserDTO.class);
        }

        return null;
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        userDao.deleteById(id);
    }
}