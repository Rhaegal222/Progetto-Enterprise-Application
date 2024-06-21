package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    ProductDTO addProduct(ProductCreateDTO productCreateDTO)throws IllegalAccessException;
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(String id);
    ProductDTO updateProduct(String id, ProductDTO productDTO) ;
    void save(ProductDTO product);
    void deleteProduct(String id) throws IllegalAccessException;

}

