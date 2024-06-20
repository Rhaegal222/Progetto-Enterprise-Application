package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.ProductCategory;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryDao extends JpaRepository<ProductCategory,String>, JpaSpecificationExecutor<ProductCategory> {

    @Query("select p from ProductCategory p where p.categoryName = :categoryName")
    Optional<ProductCategory> findByCategoryName(String categoryName);
}
