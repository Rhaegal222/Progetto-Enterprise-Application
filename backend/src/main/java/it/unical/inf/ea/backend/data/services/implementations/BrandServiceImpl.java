package it.unical.inf.ea.backend.data.services.implementations;
import it.unical.inf.ea.backend.data.dao.BrandDao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.services.interfaces.BrandService;
import it.unical.inf.ea.backend.dto.BrandDTO;
import it.unical.inf.ea.backend.dto.creation.BrandCreateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final ModelMapper modelMapper;
    private final BrandDao brandDao;

    public BrandServiceImpl(ModelMapper modelMapper, BrandDao brandDao) {
        this.modelMapper = modelMapper;
        this.brandDao = brandDao;
    }

    @Override
    public void addBrand(BrandCreateDTO brandDTO) {
        Brand brand = new Brand();
        brand.setName(brandDTO.getName());
        brand.setDescription(brandDTO.getDescription());
        Brand newBrand = brandDao.save(brand);
        modelMapper.map(newBrand, BrandDTO.class);
    }

    @Override
    public void deleteBrand(Integer id) {
        this.brandDao.deleteById(String.valueOf(id));

    }

    @Override
    public List<BrandDTO> getAllBrands() {
        List<Brand> brands = brandDao.findAll();
        return brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Brand> findBrandById(Integer id) {
        return brandDao.findByBrandId(id);
    }

    @Override
    public Optional<Brand> findBrandByName(String brandName) {
        return brandDao.findByBrandName(brandName);
    }
}