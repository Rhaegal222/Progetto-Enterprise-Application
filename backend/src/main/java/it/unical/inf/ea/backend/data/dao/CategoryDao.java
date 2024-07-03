package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryDao extends JpaRepository<Category,Long>, JpaSpecificationExecutor<Category> {

    @Query("SELECT c FROM Category c WHERE c.name = :categoryName")
    Optional<Category> findByCategoryName(String categoryName);
}
