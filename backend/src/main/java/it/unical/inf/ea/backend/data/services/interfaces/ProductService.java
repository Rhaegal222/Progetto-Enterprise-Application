package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    ProductDTO addProduct(ProductCreateDTO productCreateDTO)throws IllegalAccessException;
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDto);
    void save(ProductDTO product);
    void deleteProduct(Long id);

}

