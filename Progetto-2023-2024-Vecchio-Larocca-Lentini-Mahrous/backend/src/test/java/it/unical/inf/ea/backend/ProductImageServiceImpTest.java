package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.ProductImageDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.data.services.implementations.ProductImageServiceImp;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import it.unical.inf.ea.backend.config.FileUploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceImpTest {

    @Mock
    private ProductImageDao productImageDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductImageServiceImp productImageService;

    @Mock
    private MultipartFile multipartFile;

    private Product product;
    private ProductImage productImage;
    private static final String prodDir = System.getProperty("user.dir") + "/images/product_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";

    @BeforeEach
    public void setup() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        productImage = new ProductImage();
        productImage.setId("uuid");
        productImage.setProduct(product);
        product.setImage(productImage);
    }

    @Test
    public void testGetPhotoProductById() throws IOException {
        when(productDao.findById(1L)).thenReturn(Optional.of(product));

        Path filePath = Paths.get(imagesGetDir + "product_photos/1/photoProduct.png");
        Resource resource = new FileSystemResource(filePath);

        Resource result = productImageService.getPhotoProductById(1L);

        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.isReadable());
    }


    @Test
    public void testGetPhotoProductById_ProductNotFound() {
        when(productDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IOException.class, () -> {
            productImageService.getPhotoProductById(1L);
        });

        assertEquals("Something went wrong, try again.", exception.getMessage());
    }


    @Test
    public void testUploadInitialPhotoProductById() throws IOException {
        when(productDao.findById(1L)).thenReturn(Optional.of(product));

        when(productImageDao.save(any(ProductImage.class))).thenReturn(productImage);

        when(modelMapper.map(any(ProductImage.class), eq(ProductImageDTO.class))).thenReturn(new ProductImageDTO());

        try (MockedStatic<FileUploadUtil> mockedFileUploadUtil = mockStatic(FileUploadUtil.class)) {
            mockedFileUploadUtil.when(() -> FileUploadUtil.saveMultipartFile(anyString(), anyString(), any(MultipartFile.class)))
                    .thenAnswer(invocation -> null);

            productImageService.uploadInitialPhotoProductById(1L, multipartFile);

            verify(productImageDao, times(1)).save(any(ProductImage.class));
            mockedFileUploadUtil.verify(() -> FileUploadUtil.saveMultipartFile(eq(prodDir + "1/"), eq("photoProduct.png"), eq(multipartFile)));
        }
    }

    @Test
    public void testSaveProductImage() {
        productImageService.saveProductImage(productImage);
        verify(productImageDao, times(1)).save(productImage);
    }
}
