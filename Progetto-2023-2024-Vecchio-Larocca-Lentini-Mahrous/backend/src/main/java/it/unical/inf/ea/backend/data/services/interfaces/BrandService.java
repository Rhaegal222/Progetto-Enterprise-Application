package it.unical.inf.ea.backend.data.services.interfaces;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.dto.BrandDTO;
import it.unical.inf.ea.backend.dto.creation.BrandCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BrandService{

    void addBrand(BrandCreateDTO brandDTO);
    void deleteBrand(Long id);
    List<BrandDTO> getAllBrands();
    Optional<Brand> findBrandById(Long id);
    Optional<Brand> findBrandByName(String brandName);
}
