package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductImageService {
    Resource getImage(String url) throws IOException;

    ProductImageDTO savePhotoProduct(MultipartFile multipartFile, String product_id, String description) throws IOException, IllegalAccessException;

    void deletePhotoProduct(String id) throws IllegalAccessException, IOException;


    void saveProductImage(ProductImage productImage);
}
