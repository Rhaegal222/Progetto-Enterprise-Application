package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.CategoryDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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
    private final CategoryDao categoryDao;
    private final TokenStore tokenStore;
    private final Clock clock;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public Map<String, String> addProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException {
        LocalDateTime now = getTimeNow();
        Product product = new Product();
        product.setName(productCreateDTO.getName());
        product.setBrand(modelMapper.map(productCreateDTO.getBrand(), Brand.class));
        product.setPrice(productCreateDTO.getPrice());
        product.setDescription(productCreateDTO.getDescription());
        product.setIngredients(productCreateDTO.getIngredients());
        product.setWeight(productCreateDTO.getWeight());
        product.setNutritionalValues(productCreateDTO.getNutritionalValues());
        product.setQuantity(productCreateDTO.getQuantity());
        product.setShippingCost(productCreateDTO.getShippingCost());
        product.setAvailability(productCreateDTO.getAvailability());
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setOnSale(productCreateDTO.isOnSale());
        product.setSalePrice(productCreateDTO.getSalePrice());
        product.setCategory(modelMapper.map(productCreateDTO.getCategory(), Category.class));
        productDao.save(product);
        return Map.of("message", "Product added successfully", "productId", product.getId().toString());
    }

    public void save(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        productDao.save(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() throws IllegalAccessException {

        List<Product> products = productDao.findAll();
        return products.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Category category) {
        List<Product> products = productDao.findProductByCategory(category);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByBrand(Brand brand) {
        List<Product> products = productDao.findByBrand(brand);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByPriceRange(Double min, Double max) {
        List<Product> products = productDao.findProductsByPriceRange(min, max);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productDao.findById(id).orElse(null);
        return product != null ? modelMapper.map(product, ProductDTO.class) : null;
    }

    @Override
    public ProductDTO partialUpdateProduct(Long id, Map<String, Object> updates) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productDao.findById(id).orElseThrow();

        if (!loggedUser.isAdministrator()) {
            throw new IllegalAccessException("Only administrators can update products");
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(product);
        updates.forEach((key, value) -> {
            if (beanWrapper.isWritableProperty(key)) {
                if (value instanceof Map) {
                    if (key.equals("brand")) {
                        Brand brand = convertMapToBrand((Map<String, Object>) value);
                        beanWrapper.setPropertyValue(key, brand);
                    } else if (key.equals("category")) {
                        Category category = convertMapToCategory((Map<String, Object>) value);
                        beanWrapper.setPropertyValue(key, category);
                    }
                } else {
                    beanWrapper.setPropertyValue(key, value);
                }
            }
        });

        Product updatedProduct = productDao.save(product);
        return mapToDto(updatedProduct);
    }


    @Override
    public void deleteProduct(Long id) throws IllegalAccessException {
        Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
        productDao.delete(product);
    }

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }

    @Override
    public List<ProductDTO> getSalesProducts() {
        List<Product> products = productDao.findProductsOnSale();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public ProductDTO mapToDto(Product product){return modelMapper.map(product, ProductDTO.class);}

    private Brand convertMapToBrand(Map<String, Object> map) {
        Brand brand = new Brand();
        BeanWrapper brandWrapper = new BeanWrapperImpl(brand);
        map.forEach((key, value) -> {
            if (brandWrapper.isWritableProperty(key)) {
                brandWrapper.setPropertyValue(key, value);
            }
        });
        return brand;
    }

    private Category convertMapToCategory(Map<String, Object> map) {
        Category category = new Category();
        BeanWrapper categoryWrapper = new BeanWrapperImpl(category);
        map.forEach((key, value) -> {
            if (categoryWrapper.isWritableProperty(key)) {
                categoryWrapper.setPropertyValue(key, value);
            }
        });
        return category;
    }
}
