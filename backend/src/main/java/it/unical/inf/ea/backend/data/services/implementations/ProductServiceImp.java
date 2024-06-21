package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductCategoryDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import it.unical.inf.ea.backend.dto.enums.*;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import it.unical.inf.ea.backend.config.security.Constants;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            LocalDateTime now = getTimeNow();
            Product product = new Product();
            product.setTitle(productCreateDTO.getTitle());
            product.setProductPrice(productCreateDTO.getProductPrice());
            product.setDeliveryPrice(productCreateDTO.getDeliveryPrice());
            product.setAvailability(productCreateDTO.getAvailability());
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            ProductCategory category;
            if(productCreateDTO.getProductCategory().getId() == null) {
                category = productCategoryDao.findByCategoryName(productCreateDTO.getProductCategory().getCategoryName()).orElseThrow(EntityNotFoundException::new);
            }
            productDao.save(product);
            return modelMapper.map(product, ProductDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            productDao.save(product);
            return modelMapper.map(product, ProductDTO.class);

    }


    @Override
    public void deleteProduct(String id) throws IllegalAccessException{
        Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if ( !loggedUser.getRole().equals(UserRole.USER) )
            throw new IllegalAccessException("Only admin can delete product");
        try {
            productDao.delete(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }
}
