package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.UserDao; // Replace with your user repository interface
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.UserDTO;
import it.unical.inf.ea.backend.data.entities.User; // Replace with your user entity
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder; // If applicable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private UserDao userDao;

    private ModelMapper modelMapper;

    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDto) {

        User user = new User();

        if (userDao.findByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email già esistente");
        }
        if (userDao.findByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username già esistente");
        }
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = userDao.save(user);
        return modelMapper.map(user, UserDTO.class);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }


    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userDao.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userDao.findById(String.valueOf(id)).orElse(null);
        return user != null ? modelMapper.map(user, UserDTO.class) : null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(String.valueOf(id));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User existingUser = userDao.findById(String.valueOf(id)).orElse(null);

        if (existingUser != null) {
            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setRole(userDto.getRole());
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            existingUser = userDao.save(existingUser);

            return modelMapper.map(existingUser, UserDTO.class);
        }

        return null;
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        userDao.deleteById(String.valueOf(id));
    }
}