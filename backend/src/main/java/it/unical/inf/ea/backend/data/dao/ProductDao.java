package it.unical.inf.ea.backend.data.dao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.entities.Category;
import it.unical.inf.ea.backend.dto.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p,Category pc WHERE p.category = :category")
    List<Product> findProductByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    List<Product> findByBrand(Brand brand);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findProductsByPriceRange(Double min, Double max);

    @Query("SELECT p FROM Product p WHERE p.onSale = true")
    List<Product> findProductsOnSale();
}



