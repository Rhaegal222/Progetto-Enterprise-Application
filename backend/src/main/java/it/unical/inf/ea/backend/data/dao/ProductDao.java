package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.ProductCategory;
import it.unical.inf.ea.backend.dto.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {


    @Query("SELECT p FROM Product p WHERE p.id = :id")
    ProductDTO findProductById(String id);

    @Query("SELECT p FROM Product p,ProductCategory pc WHERE p.productCategory = :productCategory")
    List<Product> findProductByProductCategory(ProductCategory productCategory);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    List<Product> findByBrand(Brand brand);

    @Query("SELECT p FROM Product p WHERE p.productPrice BETWEEN :min AND :max")
    List<Product> findProductsByPriceRange(Double min, Double max);

    @Query("SELECT DISTINCT p.brand FROM Product p")
    List<String> findAllProductsBrands();

    @Query("SELECT p FROM Product p WHERE p.onSale = true")
    List<Product> findProductsOnSale();
}



