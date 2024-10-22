package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandDao extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    @Query("select p from Brand p where p.name = :brandName")
    Optional<Brand> findByName(String brandName);
}
