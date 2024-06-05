package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.productCategory = :productCategory")
    List<ProductDTO> findByProductCategory(ProductCategoryDTO productCategoryDTO);

    boolean isProductAvailable(String productId);

    @Query("SELECT p FROM Product p WHERE p.id = :id")
    ProductDTO findProductById(String id);


    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    List<ProductDTO> findByBrand(String brand);

    @Query("SELECT p FROM Product p WHERE p.availability = :availability")
    List<ProductDTO> findByAvailability(Availability availability);


    @Query("SELECT p FROM Product p WHERE p.productSize = :productSize")
    List<ProductDTO> findByProductSize(ProductSize productSize);


}



