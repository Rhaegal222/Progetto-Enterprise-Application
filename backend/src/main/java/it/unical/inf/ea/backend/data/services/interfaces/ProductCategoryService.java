package it.unical.inf.ea.backend.data.services.interfaces;
import it.unical.inf.ea.backend.data.entities.ProductCategory;
import  it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductCategoryService {

    void addCategory(ProductCategoryDTO productCategoryDto);
    void deleteCategory(Integer id);
    List<ProductCategoryDTO> getAllCategories();
    Optional<ProductCategory> findByCategoryId(Integer id);
    Optional<ProductCategory> findByCategoryName(String categoryName);
}
