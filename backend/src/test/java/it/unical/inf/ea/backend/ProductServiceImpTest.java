package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.data.dao.CategoryDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.implementations.ProductServiceImp;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import it.unical.inf.ea.backend.dto.enums.ProductAvailability;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @InjectMocks
    private ProductServiceImp productService;

    @Mock
    private ProductDao productDao;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserDao userDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private TokenStore tokenStore;

    @Mock
    private Clock clock;

    @Mock
    private JwtContextUtils jwtContextUtils;

    private Product product;
    private ProductDTO productDTO;
    private ProductCreateDTO productCreateDTO;
    private User defaultUser;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        defaultUser = new User();
        defaultUser.setRole(UserRole.ADMIN);

        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand Name");
        brand.setDescription("Brand Description");

        Category category = new Category();
        category.setId(1L);
        category.setName("Category Name");

        product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setDescription("Product Description");
        product.setIngredients("Ingredients");
        product.setNutritionalValues("Nutritional Values");
        product.setWeight("1kg");
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setShippingCost(BigDecimal.valueOf(10.0));
        product.setAvailability(ProductAvailability.IN_STOCK);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setOnSale(false);
        product.setBrand(brand);
        product.setCategory(category);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Product Name");
        productDTO.setDescription("Product Description");
        productDTO.setIngredients("Ingredients");
        productDTO.setNutritionalValues("Nutritional Values");
        productDTO.setWeight("1kg");
        productDTO.setQuantity(10);
        productDTO.setPrice(BigDecimal.valueOf(100.0));
        productDTO.setShippingCost(BigDecimal.valueOf(10.0));
        productDTO.setProductAvailability(ProductAvailability.IN_STOCK);
        productDTO.setBrand(new BrandDTO(1L, "Brand Name", "Brand Description"));
        productDTO.setCategory(new CategoryDTO(1L, "Category Name"));
        productDTO.setOnSale(false);

        productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("Product Name");
        productCreateDTO.setDescription("Product Description");
        productCreateDTO.setIngredients("Ingredients");
        productCreateDTO.setNutritionalValues("Nutritional Values");
        productCreateDTO.setWeight("1kg");
        productCreateDTO.setQuantity(10);
        productCreateDTO.setPrice(BigDecimal.valueOf(100.0));
        productCreateDTO.setShippingCost(BigDecimal.valueOf(10.0));
        productCreateDTO.setAvailability(ProductAvailability.IN_STOCK);
        productCreateDTO.setBrand(new BrandDTO(1L, "Brand Name", "Brand Description"));
        productCreateDTO.setCategory(new CategoryDTO(1L, "Category Name"));
        productCreateDTO.setOnSale(false);

        Mockito.lenient().when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());
        Mockito.lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void getAllProducts() throws IllegalAccessException {
        when(productDao.findAll()).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<ProductDTO> productDTOs = productService.getAllProducts();

        assertThat(productDTOs).usingRecursiveComparison().isEqualTo(List.of(productDTO));
        verify(productDao, times(1)).findAll();
    }

    @Test
    void getProductsByCategory() {
        Category category = product.getCategory();
        when(productDao.findProductByCategory(category)).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<ProductDTO> productDTOs = productService.getProductsByCategory(category);

        assertThat(productDTOs).usingRecursiveComparison().isEqualTo(List.of(productDTO));
        verify(productDao, times(1)).findProductByCategory(category);
    }

    @Test
    void getProductsByBrand() {
        Brand brand = product.getBrand();
        when(productDao.findByBrand(brand)).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<ProductDTO> productDTOs = productService.getProductsByBrand(brand);

        assertThat(productDTOs).usingRecursiveComparison().isEqualTo(List.of(productDTO));
        verify(productDao, times(1)).findByBrand(brand);
    }

    @Test
    void getProductsByPriceRange() {
        when(productDao.findProductsByPriceRange(50.0, 150.0)).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<ProductDTO> productDTOs = productService.getProductsByPriceRange(50.0, 150.0);

        assertThat(productDTOs).usingRecursiveComparison().isEqualTo(List.of(productDTO));
        verify(productDao, times(1)).findProductsByPriceRange(50.0, 150.0);
    }

    @Test
    void getProductById() {
        when(productDao.findById(product.getId())).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductDTO foundProductDTO = productService.getProductById(product.getId());

        assertThat(foundProductDTO).usingRecursiveComparison().isEqualTo(productDTO);
        verify(productDao, times(1)).findById(product.getId());
    }

    @Test
    void deleteProduct() throws IllegalAccessException {
        when(productDao.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productDao, times(1)).delete(product);
    }

    @Test
    void getSalesProducts() {
        when(productDao.findProductsOnSale()).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        List<ProductDTO> productDTOs = productService.getSalesProducts();

        assertThat(productDTOs).usingRecursiveComparison().isEqualTo(List.of(productDTO));
        verify(productDao, times(1)).findProductsOnSale();
    }
}
