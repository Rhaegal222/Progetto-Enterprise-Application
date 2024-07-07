package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.FileUploadUtil;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.ProductImageDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.data.services.interfaces.ProductImageService;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.dto.UserImageDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductImageServiceImp implements ProductImageService {
    private static final String prodDir = System.getProperty("user.dir") + "/images/product_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";


    private final ProductImageDao productImageDao;
    private final ProductDao productDao;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    @Override
    public Resource getPhotoProductById(Long productId) throws IOException {
        try {
            Product product = productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

            String myPath = imagesGetDir + "product_photos/" + product.getId() + "/photoProduct.png";

            Path filePath = Paths.get(myPath);
            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File not found or not readable at path: " + myPath);
            }
        } catch (Exception e) {
            // Log detailed error message
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error getting photo for product id: " + productId, e);
            throw new IOException("Something went wrong, try again.", e);
        }
    }



    @Override
    public void uploadInitialPhotoProductById(Long productId, MultipartFile multipartFile) throws IOException {
        Product product = productDao.findById(productId).orElseThrow(EntityNotFoundException::new);
        String fileName = "photoProduct.png";
        String localStorageDir = prodDir + productId + "/";
        ProductImage productImage = new ProductImage();
        productImage.setDescription("Photo of product " + product.getName());
        productImage.setUrlPhoto("images/product_photos/"+product.getId()+"/"+fileName);
        productImage.setProduct(product);

        FileUploadUtil.saveMultipartFile(localStorageDir, fileName, multipartFile);
        productImage = productImageDao.save(productImage);

        modelMapper.map(productImage,ProductImageDTO.class);
    }


    @Override
    public void replacePhotoProductById(Long productId, MultipartFile multipartFile) throws IOException {
        Product product = productDao.findById(productId).orElseThrow(EntityNotFoundException::new);
        String fileName = "photoProduct.png";

        ProductImage productImage = product.getImage();

        String uploadDir = prodDir + product.getId();

        FileUploadUtil.saveMultipartFile(uploadDir, fileName, multipartFile);

        String relativePath = "images/product_photos/" + product.getId() + "/" + fileName;
        productImage.setUrlPhoto(relativePath);
        productImageDao.save(productImage);

        modelMapper.map(productImage, UserImageDTO.class);
    }

    @Override
    public void deletePhotoProductById(Long productId) throws IllegalAccessException, IOException {
        Product product = productDao.findById(productId).orElseThrow(EntityNotFoundException::new);
        ProductImage productImage = product.getImage();

        productImage.setProduct(null);
        product.setImage(null);

        Path productDirPath = Paths.get(prodDir + product.getId());
        try {
            Files.walk(productDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            System.err.println("Failed to delete " + file);
                        }});
            Files.deleteIfExists(productDirPath);
        } catch (IOException e) {
            throw new IOException("Something gone wrong, try again.");
        }
        productImageDao.delete(productImage);
    }



    @Override
    public void saveProductImage(ProductImage productImage) {
        productImageDao.save(productImage);
    }

}
