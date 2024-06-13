package it.unical.inf.ea.backend.data.services.implementations;
import it.unical.inf.ea.backend.data.services.interfaces.ProductCategoryService;
import  it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.data.dao.ProductCategoryDao;
import it.unical.inf.ea.backend.data.entities.ProductCategory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ModelMapper modelMapper;
    private final ProductCategoryDao productCategoryDao;

    public ProductCategoryServiceImpl(ModelMapper modelMapper, ProductCategoryDao productCategoryDao) {
        this.modelMapper = modelMapper;
        this.productCategoryDao = productCategoryDao;
    }

    @Override
    public ProductCategoryDTO addCategory(ProductCategoryDTO productCategoryDto) {

        if (productCategoryDao.findByCategoryName(productCategoryDto.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException("la Categoria è già esistente");
        }
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(productCategoryDto.getCategoryName());


        ProductCategory newProductCategory = productCategoryDao.save(productCategory);
        return modelMapper.map(newProductCategory, ProductCategoryDTO.class);
    }

    @Override
    public void deleteCategory(String id) {
        this.productCategoryDao.deleteById(id);
    }

    @Override
    public List<ProductCategoryDTO> getAllCategories() {
        List<ProductCategory> ProductCategories = productCategoryDao.findAll();
        return ProductCategories.stream()
                .map(ProductCategory -> modelMapper.map(ProductCategory, ProductCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductCategory> findByCategoryId(String id) {
        return productCategoryDao.findById(id);
    }

    @Override
    public Optional<ProductCategory> findByCategoryName(String categoryName) {
        return productCategoryDao.findByCategoryName(categoryName);
    }

}