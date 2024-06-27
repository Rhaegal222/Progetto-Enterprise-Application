package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.ProductCategory;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {
    ProductDTO addProduct(ProductCreateDTO productCreateDTO)throws IllegalAccessException;
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategory(ProductCategory productCategory);
    List<ProductDTO> getProductsByBrand(Brand brand);
    String saveImage(MultipartFile image, String id);
    List<ProductDTO> getProductsByPriceRange(Double min, Double max);
    ProductDTO getProductById(String id) throws IllegalAccessException;
    ProductDTO updateProduct(String id, ProductDTO productDTO) throws IllegalAccessException ;
    void save(ProductDTO product);
    void deleteProduct(String id) throws IllegalAccessException;

}

