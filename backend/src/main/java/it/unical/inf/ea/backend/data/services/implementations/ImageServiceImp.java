package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.ProductImageDao;
import it.unical.inf.ea.backend.data.dao.UserImageDao;
import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.data.services.interfaces.ImageService;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.dto.UserImageDTO;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";
    private static final String prodDir = System.getProperty("user.dir") + "/images/product_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir") + "/images/";


    private final ProductImageDao productImageDao;
    private final UserImageDao userImageDao;
    private final ProductDao productDao;
    //private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    @Override
    public Resource getImage(String url) throws IOException {
        try {
            String myPath = imagesGetDir + url;

            Path filePath = Paths.get(myPath);

            return new FileSystemResource(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Something gone wrong, try again.");
        }
    }

    @Override
    public UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getPhotoProfile()!=null)
            throw new IllegalAccessException("Cannot upload more photo, replace previous photo");

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())).replace(":", "");

        String localStorageDir = userDir + loggedUser.getUsername();
        UserImage userImage = new UserImage();
        userImage.setDescription(description);
        userImage.setUrlPhoto("images/user_photos/"+loggedUser.getUsername()+"/"+fileName);
        userImage.setUser(loggedUser);
        loggedUser.setPhotoProfile(userImage);

        FileUploadServiceImp.saveFile(localStorageDir, fileName, multipartFile);
        userImage= userImageDao.save(userImage);

        return modelMapper.map(userImage,UserImageDTO.class);
         */
        return null;
    }

    @Override
    public void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(":", "");

        UserImage userImage = userImageDao.findById(id).orElseThrow(EntityNotFoundException::new);

        String uploadDir = userDir + loggedUser.getUsername();

        FileUploadServiceImp.saveFile(uploadDir, fileName, multipartFile);

        userImage.setUrlPhoto(uploadDir+"/"+fileName);
        userImageDao.save(userImage);
         */
    }

    @Override
    public void deletePhotoUser(String id) throws IllegalAccessException {
        /*
    }
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        UserImage userImage = userImageDao.findById(id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getPhotoProfile().getId().equals(userImage.getId()))
            throw new IllegalAccessException("Cannot delete image of others");
        userImage.setUser(null);
        loggedUser.setPhotoProfile(null);

        userImageDao.delete(userImage);
         */
    }

    @Override
    public ProductImageDTO saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productDao.findById(product_id).orElseThrow(EntityNotFoundException::new);;

        ProductImage productImage = new ProductImage();


        return localProductImageSave(product,multipartFile,productImage,description);
         */
        return null;
    }

    public ProductImageDTO localProductImageSave(Product product, MultipartFile multipartFile, ProductImage productImage, String description) {
        /*
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(":", "");

        try{
            String localStorageDir = prodDir +product.getId();
            productImage.setDescription(description);
            productImage.setUrlPhoto("images/product_photos/"+product.getId()+"/"+fileName);
            productImage.setProduct(product);
            FileUploadServiceImp.saveFile(localStorageDir, fileName, multipartFile);
            productImageDao.save(productImage);

            return modelMapper.map(productImage,ProductImageDTO.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
         */
        return null;
    }

    @Override
    public void replaceImageProduct(String id, MultipartFile multipartFile) throws IOException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        ProductImage productImage = productImageDao.findById(id).orElseThrow(EntityNotFoundException::new);

        String uploadDir = prodDir + loggedUser.getUsername();

        FileUploadServiceImp.saveFile(uploadDir, fileName, multipartFile);

        productImage.setUrlPhoto(uploadDir+"/"+fileName);
        productImageDao.save(productImage);
        */
    }

    @Override
    public void deleteImageProduct(String id) throws IllegalAccessException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        ProductImage productImage = productImageDao.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = productImage.getProduct();

        if(loggedUser.getRole().equals(UserRole.USER) && (!loggedUser.getId().equals(product.getSeller().getId()) || !product.getProductImages().contains(productImage)))
            throw new IllegalAccessException("Cannot delete image of others");

        productImageDao.delete(productImage);
         */
    }

    @Override
    public void saveUserImage(UserImage userImage) {
        userImageDao.save(userImage);
    }
}
