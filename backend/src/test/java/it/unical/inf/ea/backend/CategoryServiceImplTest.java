package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.data.dao.CategoryDao;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.data.services.implementations.CategoryServiceImpl;
import it.unical.inf.ea.backend.dto.CategoryDTO;
import it.unical.inf.ea.backend.dto.creation.CategoryCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryCreateDTO defaultCategoryCreateDTO;
    private CategoryDTO defaultCategoryDTO;
    private Category defaultCategoryEntity;

    @BeforeEach
    public void setUp() {
        defaultCategoryCreateDTO = CategoryCreateDTO.builder()
                .name("categoryName")
                .build();

        defaultCategoryEntity = new Category();
        defaultCategoryEntity.setId(1L);
        defaultCategoryEntity.setName("categoryName");

        defaultCategoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("categoryName")
                .build();
    }

    @Test
    void mapEntityToDTO() {
        when(modelMapper.map(defaultCategoryEntity, CategoryDTO.class)).thenReturn(defaultCategoryDTO);

        CategoryDTO categoryDTO = mapToDTO(defaultCategoryEntity);

        assertThat(categoryDTO).usingRecursiveComparison().isEqualTo(defaultCategoryDTO);
    }

    @Test
    void mapDTOToEntity() {
        when(modelMapper.map(defaultCategoryDTO, Category.class)).thenReturn(defaultCategoryEntity);

        Category category = mapToEntity(defaultCategoryDTO);

        assertThat(category).usingRecursiveComparison().isEqualTo(defaultCategoryEntity);
    }

    @Test
    void addCategory() {
        when(categoryDao.save(any(Category.class))).thenReturn(defaultCategoryEntity);
        when(modelMapper.map(defaultCategoryEntity, CategoryDTO.class)).thenReturn(defaultCategoryDTO);

        categoryService.addCategory(defaultCategoryCreateDTO);

        verify(categoryDao, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory() {
        doNothing().when(categoryDao).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryDao, times(1)).deleteById(1L);
    }

    @Test
    void getAllCategories() {
        List<Category> categories = List.of(defaultCategoryEntity);
        when(categoryDao.findAll()).thenReturn(categories);
        when(modelMapper.map(defaultCategoryEntity, CategoryDTO.class)).thenReturn(defaultCategoryDTO);

        List<CategoryDTO> categoryDTOs = categoryService.getAllCategories();

        assertThat(categoryDTOs).usingRecursiveComparison().isEqualTo(List.of(defaultCategoryDTO));
    }

    @Test
    void findCategoryById_existingId() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(defaultCategoryEntity));

        Optional<Category> category = categoryService.findByCategoryId(1L);

        assertThat(category).isPresent();
        assertThat(category.get()).usingRecursiveComparison().isEqualTo(defaultCategoryEntity);
    }

    @Test
    void findCategoryById_nonExistentId() {
        when(categoryDao.findById(2L)).thenReturn(Optional.empty());

        Optional<Category> category = categoryService.findByCategoryId(2L);

        assertThat(category).isNotPresent();
    }

    @Test
    void findCategoryByName_existingName() {
        when(categoryDao.findByCategoryName("categoryName")).thenReturn(Optional.of(defaultCategoryEntity));

        Optional<Category> category = categoryService.findByCategoryName("categoryName");

        assertThat(category).isPresent();
        assertThat(category.get()).usingRecursiveComparison().isEqualTo(defaultCategoryEntity);
    }

    @Test
    void findCategoryByName_nonExistentName() {
        when(categoryDao.findByCategoryName("nonExistentName")).thenReturn(Optional.empty());

        Optional<Category> category = categoryService.findByCategoryName("nonExistentName");

        assertThat(category).isNotPresent();
    }

    private Category mapToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}
