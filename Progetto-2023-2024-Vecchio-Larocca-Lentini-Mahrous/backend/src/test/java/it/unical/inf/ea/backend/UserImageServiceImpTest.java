package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.dao.UserImageDao;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.UserImage;
import it.unical.inf.ea.backend.data.services.implementations.UserImageServiceImp;
import it.unical.inf.ea.backend.dto.UserImageDTO;
import it.unical.inf.ea.backend.config.FileUploadUtil;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserImageServiceImpTest {

    @Mock
    private UserImageDao userImageDao;

    @Mock
    private UserDao userDao;

    @Mock
    private JwtContextUtils jwtContextUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserImageServiceImp userImageService;

    @Mock
    private MultipartFile multipartFile;

    private User user;
    private UserImage userImage;
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("TestUser");

        userImage = new UserImage();
        userImage.setId(UUID.randomUUID().toString());
        userImage.setUser(user);
        user.setImage(userImage);
    }

    @Test
    public void testGetPhotoProfileById_UserNotFound() {
        when(userDao.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(IOException.class, () -> {
            userImageService.getPhotoProfileById(UUID.randomUUID().toString());
        });

        assertEquals("Something gone wrong, try again.", exception.getMessage());
    }

    @Test
    public void testUploadInitialPhotoProfile() throws IOException {
        when(userDao.findById(any(UUID.class))).thenReturn(Optional.of(user));

        when(userImageDao.save(any(UserImage.class))).thenReturn(userImage);

        when(modelMapper.map(any(UserImage.class), eq(UserImageDTO.class))).thenReturn(new UserImageDTO());

        try (MockedStatic<FileUploadUtil> mockedFileUploadUtil = mockStatic(FileUploadUtil.class)) {
            mockedFileUploadUtil.when(() -> FileUploadUtil.saveMultipartFile(anyString(), anyString(), any(MultipartFile.class)))
                    .thenAnswer(invocation -> null);

            userImageService.uploadInitialPhotoProfile(user.getId(), multipartFile);

            verify(userImageDao, times(1)).save(any(UserImage.class));
            mockedFileUploadUtil.verify(() -> FileUploadUtil.saveMultipartFile(eq(userDir + user.getId() + "/"), eq("photoProfile.png"), eq(multipartFile)));
        }
    }

    @Test
    public void testSaveUserImage() {
        userImageService.saveUserImage(userImage);
        verify(userImageDao, times(1)).save(userImage);
    }

    @Test
    public void testDeleteMyPhotoProfile() throws IllegalAccessException, IOException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(user);

        Path userDirPath = Paths.get(userDir + user.getId());
        Files.createDirectories(userDirPath);

        userImageService.deleteMyPhotoProfile();

        verify(userImageDao, times(1)).delete(userImage);
        assertFalse(Files.exists(userDirPath));
    }

    @Test
    public void testReplaceMyPhotoProfile() throws IOException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(user);

        try (MockedStatic<FileUploadUtil> mockedFileUploadUtil = mockStatic(FileUploadUtil.class)) {
            mockedFileUploadUtil.when(() -> FileUploadUtil.saveMultipartFile(anyString(), anyString(), any(MultipartFile.class)))
                    .thenAnswer(invocation -> null);

            userImageService.replaceMyPhotoProfile(multipartFile);

            verify(userImageDao, times(1)).save(userImage);
            mockedFileUploadUtil.verify(() -> FileUploadUtil.saveMultipartFile(eq(userDir + user.getId()), eq("photoProfile.png"), eq(multipartFile)));
        }
    }

}
