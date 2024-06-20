package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    User findByEmail(String email);

    Page<User> findAllByUsernameContainingIgnoreCase(Pageable pageable, String username);

    Page<User> findAllByRoleEqualsAndUsernameContains(Pageable pageable, UserRole userRole, String username);

    Page<User> findAllByRoleEqualsOrRoleEquals(Pageable pageable, UserRole userRole, UserRole userRole1);

}
