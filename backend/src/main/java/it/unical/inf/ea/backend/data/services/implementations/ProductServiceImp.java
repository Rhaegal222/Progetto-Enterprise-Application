package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductCategoryDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductDao productDao;
    private final ModelMapper modelMapper;
    private final UserDao userDao;
    private final ProductCategoryDao productCategoryDao;
    private final TokenStore tokenStore;
    private final Clock clock;
    private final JwtContextUtils jwtContextUtils;


    @Override
    public ProductDTO addProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException {
            LocalDateTime now = getTimeNow();
            Product product = new Product();
            product.setTitle(productCreateDTO.getTitle());
            product.setBrand(productCreateDTO.getBrand());
            product.setProductPrice(productCreateDTO.getProductPrice());
            product.setDescription(productCreateDTO.getDescription());
            product.setIngredients(productCreateDTO.getIngredients());
            product.setProductWeight(productCreateDTO.getProductWeight());
            product.setNutritionalValues(productCreateDTO.getNutritionalValues());
            product.setQuantity(productCreateDTO.getQuantity());
            product.setDeliveryPrice(productCreateDTO.getDeliveryPrice());
            product.setAvailability(productCreateDTO.getAvailability());
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            product.setProductCategory(modelMapper.map(productCreateDTO.getProductCategory(), ProductCategory.class));
            productDao.save(product);
            return modelMapper.map(product, ProductDTO.class);

    }

    public void save(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        productDao.save(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productDao.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(ProductCategory productCategory) {
        List<Product> products = productDao.findProductByProductCategory(productCategory);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByBrand(String brand) {
        List<Product> products = productDao.findByBrand(brand);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllProductsBrands() {
        List<String> brands = productDao.findAllProductsBrands();
        return new ArrayList<>(brands);
    }
    /*
        @Override
    public List<ProductCategoryDTO> getAllCategories() {
        List<ProductCategory> ProductCategories = productCategoryDao.findAll();
        return ProductCategories.stream()
                .map(ProductCategory -> modelMapper.map(ProductCategory, ProductCategoryDTO.class))
                .collect(Collectors.toList());
    }
     */

    @Override
    public List<ProductDTO> getProductsByPriceRange(Double min, Double max) {
        List<Product> products = productDao.findProductsByPriceRange(min, max);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productDao.findById(String.valueOf(id)).orElse(null);
        return product != null ? modelMapper.map(product, ProductDTO.class) : null;
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {

            Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
            LocalDateTime now = getTimeNow();
            product.setTitle(productDTO.getTitle());
            product.setDescription(productDTO.getDescription());
            product.setBrand(productDTO.getBrand());
            product.setIngredients(productDTO.getIngredients());
            product.setNutritionalValues(productDTO.getNutritionalValues());
            product.setProductPrice(productDTO.getProductPrice());
            product.setDeliveryPrice(productDTO.getDeliveryPrice());
            product.setAvailability(productDTO.getAvailability());
            product.setQuantity(productDTO.getQuantity());
            product.setProductWeight(productDTO.getProductWeight());
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            ProductCategory category;
            if(productDTO.getProductCategory().getId() == null) {
                category = productCategoryDao.findByCategoryName(productDTO.getProductCategory().getCategoryName()).orElseThrow(EntityNotFoundException::new);
            }

            productDao.save(product);
            return modelMapper.map(product, ProductDTO.class);

    }


    @Override
    public void deleteProduct(String id) throws IllegalAccessException{
        Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
        productDao.delete(product);
    }

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }
}
