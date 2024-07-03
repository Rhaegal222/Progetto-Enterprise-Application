package it.unical.inf.ea.backend.data.services.implementations;
import it.unical.inf.ea.backend.data.services.interfaces.CategoryService;
import it.unical.inf.ea.backend.dto.CategoryDTO;
import it.unical.inf.ea.backend.data.dao.CategoryDao;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.dto.creation.CategoryCreateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ModelMapper modelMapper;
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(ModelMapper modelMapper, CategoryDao categoryDao) {
        this.modelMapper = modelMapper;
        this.categoryDao = categoryDao;
    }

    @Override
    public void addCategory(CategoryCreateDTO createCategoryDTO) {

        Category category = new Category();
        category.setName(createCategoryDTO.getCategoryName());

        Category newCategory = categoryDao.save(category);
        modelMapper.map(newCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Integer id) {
        this.categoryDao.deleteById(String.valueOf(id));
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> ProductCategories = categoryDao.findAll();
        return ProductCategories.stream()
                .map(ProductCategory -> modelMapper.map(ProductCategory, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByCategoryId(Integer id) {
        return categoryDao.findById(String.valueOf(id));
    }

    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        return categoryDao.findByCategoryName(categoryName);
    }
}