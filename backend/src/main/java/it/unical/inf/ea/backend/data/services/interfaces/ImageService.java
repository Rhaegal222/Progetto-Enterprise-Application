package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.dto.UserImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Resource getImage(String url) throws IOException;

    UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException;

    void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException;

    void deletePhotoUser(String id) throws IllegalAccessException;

    ProductImageDTO saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException;

    void replaceImageProduct(String id, MultipartFile multipartFile) throws IOException;

    void deleteImageProduct(String id) throws IllegalAccessException;

    void saveUserImage(UserImage userImage);
}
