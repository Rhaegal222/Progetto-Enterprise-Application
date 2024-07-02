package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.FileUploadUtil;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.ProductImageDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.ProductImageService;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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




@Service
@RequiredArgsConstructor
public class ProductImageServiceImp implements ProductImageService {
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";

    private static final String prodDir = System.getProperty("user.dir") + "/images/product_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";


    private final ProductImageDao productImageDao;
    private final ProductDao productDao;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    @Override
    public Resource getImage(String url) throws IOException {
        try{
            String myPath=imagesGetDir+url;

            Path filePath = Paths.get(myPath);

            return new FileSystemResource(filePath);
        }catch (Exception e){
            e.printStackTrace();
            throw new IOException("Something gone wrong, try again.");
        }
    }

    @Override
    public ProductImageDTO savePhotoProduct(MultipartFile multipartFile, String product_id, String description) throws IOException, IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) )
            throw new IllegalAccessException("only admin can upload photos");

        Product product = productDao.findById(product_id).orElseThrow(EntityNotFoundException::new);
        ProductImage productImage = new ProductImage();

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(":", "");

        String localStorageDir = prodDir +product.getId();
        productImage.setDescription(description);
        productImage.setUrlPhoto("images/product_photos/"+product.getId()+"/"+fileName);
        productImage.setProduct(product);
        FileUploadUtil.saveMultipartFile(localStorageDir, fileName, multipartFile);
        productImageDao.save(productImage);

        return modelMapper.map(productImage,ProductImageDTO.class);
    }

    @Override
    public void deletePhotoProduct(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        ProductImage productImage = productImageDao.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = productImage.getProduct();

        // Check if the logged user has permissions to delete the image
        if(loggedUser.getRole().equals(UserRole.USER) ) {
            throw new IllegalAccessException("only admin can delete photos");
        }

        // Unlink product and photo
        productImage.setProduct(null);
        product.setPhotoProduct(null);

        Path productDirPath = Paths.get(userDir + product.getId());
        try {
            // Delete all files in the directory
            Files.walk(productDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            System.err.println("Failed to delete " + file);
                        }
                    });

            // Finally, delete the directory itself if it's empty
            Files.deleteIfExists(productDirPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalAccessException("Failed to delete product's photo directory");
        }

        // Remove the image record from the database
        productImageDao.delete(productImage);
    }



    @Override
    public void saveProductImage(ProductImage productImage) {
        productImageDao.save(productImage);
    }

}
