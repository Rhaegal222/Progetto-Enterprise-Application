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
        //user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Codifica la password
        user = userDao.save(user);
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
    public UserDTO updateUser(String id, UserDTO userDto) {
        User existingUser = userDao.findById(id).orElse(null);

        if (existingUser != null) {
            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setRole(userDto.getRole());

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