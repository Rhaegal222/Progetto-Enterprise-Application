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

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }


    @Override
    public List<ProductDTO> getAllProducts() {
        return List.of();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return null;
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        return null;
    }

    @Override
    public void save(ProductDTO product) {

    }

    @Override
    public void deleteProduct(Long id) {

    }
}
