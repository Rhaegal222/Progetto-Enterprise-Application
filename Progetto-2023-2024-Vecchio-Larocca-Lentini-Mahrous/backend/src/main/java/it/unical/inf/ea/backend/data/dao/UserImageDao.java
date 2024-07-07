package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImageDao extends JpaRepository<UserImage, String>, JpaSpecificationExecutor<UserImage> {
    boolean existsByUser(User user);
    UserImage findByUserId(UUID userId);
}
