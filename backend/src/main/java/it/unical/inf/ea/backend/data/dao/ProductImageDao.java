package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageDao extends JpaRepository<ProductImage,String>, JpaSpecificationExecutor<ProductImage> {
}
