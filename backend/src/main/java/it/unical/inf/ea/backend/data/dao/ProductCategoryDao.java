package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryDao extends JpaRepository<ProductCategory,String>, JpaSpecificationExecutor<ProductCategory> {
    Boolean existsByIdEqualsAndPrimaryCatEqualsAndSecondaryCatEqualsAndTertiaryCatEquals(String id, String primaryCat, String secondaryCat, String tertiaryCat);
    List<ProductCategory> findAllByPrimaryCat(String primaryCat);

    List<ProductCategory> findAllBySecondaryCat(String secondaryCat);

    ProductCategory findProductCategoryByTertiaryCat(String tertiaryCat);
}
