package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {


    ProductDTO createProduct(ProductDTO productDto);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(Long id);

    Optional<User> findProductById(Long id);

    ProductDTO updateProduct(Long id, ProductDTO productDto);

    void deleteProduct(Long id);

}

