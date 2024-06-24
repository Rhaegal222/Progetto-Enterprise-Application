package it.unical.inf.ea.backend.data.services.interfaces;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.dto.BrandDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BrandService{

    void addBrand(BrandDTO brandDTO);
    void deleteBrand(Integer id);
    List<BrandDTO> getAllBrands();
    Optional<Brand> findBrandById(Integer id);
    Optional<Brand> findBrandByName(String brandName);
}
