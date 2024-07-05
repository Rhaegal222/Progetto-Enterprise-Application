package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProductImageService {
    Resource getPhotoProductById(Long productId) throws IOException;
    void uploadInitialPhotoProductById(Long productId, MultipartFile multipartFile) throws IOException;
    void replacePhotoProductById(Long productId, MultipartFile multipartFile) throws IOException;
    void deletePhotoProductById(Long productId) throws IllegalAccessException, IOException;
    void saveProductImage(ProductImage productImage);
}
