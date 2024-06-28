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
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getPhotoProfile() != null) {
            throw new IllegalAccessException("Cannot upload more photos, replace the previous photo.");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())).replace(":", "");
        String localStorageDir = userDir + loggedUser.getUsername();
        UserImage userImage = new UserImage();
        userImage.setDescription(description);
        userImage.setUrlPhoto("images/user_photos/" + loggedUser.getUsername() + "/" + fileName);
        userImage.setUser(loggedUser);
        loggedUser.setPhotoProfile(userImage);

        FileUploadUtil.saveFile(localStorageDir, fileName, multipartFile);
        userImage = userImageDao.save(userImage);

        return modelMapper.map(userImage, UserImageDTO.class);
    }
    @Override
    public void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(":", "");

        UserImage userImage = userImageDao.findById(id).orElseThrow(EntityNotFoundException::new);

        String uploadDir = userDir + loggedUser.getUsername();


        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        userImage.setUrlPhoto(uploadDir+"/"+fileName);
        userImageDao.save(userImage);
    }

    @Override
    public void deletePhotoUser(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        UserImage userImage = userImageDao.findById(id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getPhotoProfile().getId().equals(userImage.getId()))
            throw new IllegalAccessException("Cannot delete image of others");
        userImage.setUser(null);
        loggedUser.setPhotoProfile(null);

        userImageDao.delete(userImage);
    }


    @Override
    public void saveUserImage(UserImage userImage) {
        userImageDao.save(userImage);
    }

}
