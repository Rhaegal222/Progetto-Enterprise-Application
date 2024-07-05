package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.FileUploadUtil;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.dao.UserImageDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.data.services.interfaces.UserImageService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserImageServiceImp implements UserImageService {
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";

    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";

    private final UserImageDao userImageDao;
    private final UserDao userDao;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    @Override
    public Resource getPhotoProfileById(String userId) throws IOException {
        try{
            User user = userDao.findById(UUID.fromString(userId)).orElseThrow(EntityNotFoundException::new);

            String myPath=imagesGetDir+ "user_photos/" + user.getId() + "/photoProfile.png";

            Path filePath = Paths.get(myPath);

            return new FileSystemResource(filePath);
        }catch (Exception e){
            throw new IOException("Something gone wrong, try again.");
        }
    }

    @Override
    public Resource getMyPhotoProfile() throws IOException {
        try{
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();

            String myPath=imagesGetDir+ "user_photos/" + loggedUser.getId() + "/photoProfile.png";

            Path filePath = Paths.get(myPath);

            return new FileSystemResource(filePath);
        }catch (Exception e){
            e.printStackTrace();
            throw new IOException("Something gone wrong, try again.");
        }
    }

    @Override
    public void uploadInitialPhotoProfile(UUID userId, MultipartFile multipartFile) throws IOException {
        User user = userDao.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String fileName = "photoProfile.png";
        String localStorageDir = userDir + userId + "/";
        UserImage userImage = new UserImage();
        userImage.setDescription("Profile photo of user " + user.getUsername());
        userImage.setUrlPhoto("images/user_photos/" + user.getId() + "/" + fileName);
        userImage.setUser(user);
        user.setImage(userImage);

        FileUploadUtil.saveMultipartFile(localStorageDir, fileName, multipartFile);
        userImage = userImageDao.save(userImage);

        modelMapper.map(userImage, UserImageDTO.class);
    }

    @Override
    public void replaceMyPhotoProfile(MultipartFile multipartFile) throws IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = "photoProfile.png";

        UserImage userImage = loggedUser.getImage();

        String uploadDir = userDir + loggedUser.getId();

        FileUploadUtil.saveMultipartFile(uploadDir, fileName, multipartFile);

        String relativePath = "images/user_photos/" + loggedUser.getId() + "/" + fileName;
        userImage.setUrlPhoto(relativePath);
        userImageDao.save(userImage);

        modelMapper.map(userImage, UserImageDTO.class);
    }

    @Override
    public void deleteMyPhotoProfile() throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        UserImage userImage = loggedUser.getImage();

        userImage.setUser(null);
        loggedUser.setImage(null);

        Path userDirPath = Paths.get(userDir + loggedUser.getId());
        try {
            Files.walk(userDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            System.err.println("Failed to delete " + file);
                        }
                    });

            Files.deleteIfExists(userDirPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalAccessException("Failed to delete user's photo directory");
        }
        userImageDao.delete(userImage);
    }



    @Override
    public void saveUserImage(UserImage userImage) {
        userImageDao.save(userImage);
    }

}
