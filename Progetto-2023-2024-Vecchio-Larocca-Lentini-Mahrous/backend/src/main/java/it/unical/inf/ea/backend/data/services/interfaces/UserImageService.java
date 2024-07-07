package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.UserImage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserImageService {
    Resource getPhotoProfileById(String userId) throws IOException;
    Resource getMyPhotoProfile() throws IOException;

    void uploadInitialPhotoProfile(UUID userId, MultipartFile multipartFile) throws IOException, IllegalAccessException;

    void replaceMyPhotoProfile(MultipartFile multipartFile) throws IOException;


    void deleteMyPhotoProfile() throws IllegalAccessException;

    void saveUserImage(UserImage userImage);
}
