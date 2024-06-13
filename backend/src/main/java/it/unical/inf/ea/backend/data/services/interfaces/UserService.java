package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {


    UserDTO createUser(UserDTO userDto);
    Optional<User> findByEmail(String email);
    List<UserDTO> getAllUsers();
    Optional<User> findById(String id);
    void save(User user);
    void deleteUser(String id);
}