package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.FileUploadUtil;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.UserImageDao;
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

@Service
@RequiredArgsConstructor
public class UserImageServiceImp implements UserImageService {
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";


    private final UserImageDao userImageDao;
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
    public void savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getPhotoProfile() != null) {
            throw new IllegalAccessException("Cannot upload more photos, replace the previous photo.");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())).replace(":", "");
        String localStorageDir = userDir + loggedUser.getId();
        UserImage userImage = new UserImage();
        userImage.setDescription(description);
        userImage.setUrlPhoto("images/user_photos/" + loggedUser.getId() + "/" + fileName);
        userImage.setUser(loggedUser);
        loggedUser.setPhotoProfile(userImage);

        FileUploadUtil.saveMultipartFile(localStorageDir, fileName, multipartFile);
        userImage = userImageDao.save(userImage);

        modelMapper.map(userImage, UserImageDTO.class);
    }

    @Override
    public void deletePhotoUser(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        UserImage userImage = userImageDao.findById(id).orElseThrow(EntityNotFoundException::new);

        // Check if the logged user has permissions to delete the image
        if(loggedUser.getRole().equals(UserRole.USER) &&
                !loggedUser.getPhotoProfile().getId().equals(userImage.getId())) {
            throw new IllegalAccessException("Cannot delete image of others");
        }

        // Unlink user and photo
        userImage.setUser(null);
        loggedUser.setPhotoProfile(null);

        Path userDirPath = Paths.get(userDir + loggedUser.getId());
        try {
            // Delete all files in the directory
            Files.walk(userDirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            System.err.println("Failed to delete " + file);
                        }
                    });

            // Finally, delete the directory itself if it's empty
            Files.deleteIfExists(userDirPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalAccessException("Failed to delete user's photo directory");
        }

        // Remove the image record from the database
        userImageDao.delete(userImage);
    }



    @Override
    public void saveUserImage(UserImage userImage) {
        userImageDao.save(userImage);
    }

}
