package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.UserImage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserImageService {
    Resource getImage(String url) throws IOException;

    void uploadImage(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException;

    void deleteImage(UUID id) throws IllegalAccessException;


    void saveUserImage(UserImage userImage);
}
