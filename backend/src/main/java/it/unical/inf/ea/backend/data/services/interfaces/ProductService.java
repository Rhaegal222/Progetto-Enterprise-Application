package it.unical.inf.ea.backend.data.services.interfaces;


import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.dto.CapabilityDTO;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException;
    ProductDTO updateProduct(String id, ProductDTO productDTO) throws IllegalAccessException;
    void deleteProduct(String id) throws IllegalAccessException;

    ProductDTO getProductById(String id, boolean capability);

    String getCapabilityUrl(String productId);

    CapabilityDTO getCapability(String productId);


    Iterable<ProductCategoryDTO> getCategoriesList();

    Iterable<String> getPrimaryCategoriesList();

    Iterable<String> getSecondaryCategoriesListByPrimaryCat(String primaryCategory);

    Iterable<String> getTertiaryCategoriesListBySecondaryCat(String secondaryCategory);
    String getCategoryId(String category);

    Page<ProductBasicDTO> getProductFilteredPage(Specification<Product> withFilters, int page, int size, String sortBy, String sortDirection);

    Page<ProductBasicDTO> getMyProducts(int page, int size);

    ProductBasicDTO getProductBasicById(String id, boolean capability);

    String getCapabilityToken(String productId);
}

