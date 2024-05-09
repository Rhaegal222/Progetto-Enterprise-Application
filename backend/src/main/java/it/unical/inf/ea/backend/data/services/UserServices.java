package it.unical.inf.ea.backend.data.services;

import java.util.Collection;
import java.util.Optional;

import it.unical.inf.ea.backend.dto.basics.OrderBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserServices {
    UserDTO createUser(User user);
    // TO DO
    // void deleteUser(String id);

    UserBasicDTO findUserById(String id);
    Optional<UserBasicDTO> findBasicByUsername(String username);

    Page<UserBasicDTO> searchUsersByUsername(String usernameQuery, int page, int size);
    Optional<UserDTO> findByUsername(String username);
    ResponseEntity<String> registerUser(String username, String email, String password);
    void createUser(String username, String password, String email);
    Page<OrderBasicDTO> getMyOrders(int page, int size);
    UserDTO banUser(String userId);

}