package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserDTO createUser(UserDTO userDto);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(String id);

    UserDTO updateUser(String id, UserDTO userDto);

    void deleteUser(String id);
}