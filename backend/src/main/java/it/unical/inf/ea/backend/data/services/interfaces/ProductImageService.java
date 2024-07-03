package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProductImageService {
    Resource getImage(String url) throws IOException;

    ProductImageDTO savePhotoProduct(MultipartFile multipartFile, Long productId, String description) throws IOException, IllegalAccessException;

    void deletePhotoProduct(UUID id) throws IllegalAccessException, IOException;


    void saveProductImage(ProductImage productImage);
}
