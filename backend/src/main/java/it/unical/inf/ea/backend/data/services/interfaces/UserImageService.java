package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.dto.UserImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserImageService {
    Resource getImage(String url) throws IOException;

    UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException;

    void deletePhotoUser(String id) throws IllegalAccessException;


    void saveUserImage(UserImage userImage);
}
