package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
    Map<String, String> addProduct(ProductCreateDTO productCreateDTO)throws IllegalAccessException;
    List<ProductDTO> getAllProducts() throws IllegalAccessException;
    List<ProductDTO> getProductsByCategory(Category category);
    List<ProductDTO> getProductsByBrand(Brand brand);
    List<ProductDTO> getProductsByPriceRange(Double min, Double max);
    ProductDTO getProductById(Long id) throws IllegalAccessException;
    ProductDTO partialUpdateProduct(Long id, Map<String, Object> updates) throws IllegalAccessException ;
    void save(ProductDTO product);
    void deleteProduct(Long id) throws IllegalAccessException;
    List<ProductDTO> getSalesProducts();

}

