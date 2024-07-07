package it.unical.inf.ea.backend.data.services.interfaces;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.dto.CategoryDTO;
import it.unical.inf.ea.backend.dto.creation.CategoryCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {

    void addCategory(CategoryCreateDTO category);
    void deleteCategory(Long id);
    List<CategoryDTO> getAllCategories();
    Optional<Category> findByCategoryId(Long id);
    Optional<Category> findByCategoryName(String categoryName);
}
