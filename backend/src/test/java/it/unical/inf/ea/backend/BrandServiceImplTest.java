package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.data.dao.BrandDao;
import it.unical.inf.ea.backend.data.entities.Brand;
import it.unical.inf.ea.backend.data.services.implementations.BrandServiceImpl;
import it.unical.inf.ea.backend.dto.BrandDTO;
import it.unical.inf.ea.backend.dto.creation.BrandCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BrandServiceImplTest {

    @Mock
    private BrandDao brandDao;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    private BrandCreateDTO defaultBrandCreateDTO;
    private BrandDTO defaultBrandDTO;
    private Brand defaultBrandEntity;

    @BeforeEach
    public void setUp() {
        defaultBrandCreateDTO = BrandCreateDTO.builder()
                .name("brandName")
                .description("brandDescription")
                .build();

        defaultBrandEntity = new Brand();
        defaultBrandEntity.setId(1L);
        defaultBrandEntity.setName("brandName");
        defaultBrandEntity.setDescription("brandDescription");

        defaultBrandDTO = BrandDTO.builder()
                .id(1L)
                .name("brandName")
                .description("brandDescription")
                .build();
    }

    @Test
    void mapEntityToDTO() {
        when(modelMapper.map(defaultBrandEntity, BrandDTO.class)).thenReturn(defaultBrandDTO);

        BrandDTO brandDTO = mapToDTO(defaultBrandEntity);

        assertThat(brandDTO).usingRecursiveComparison().isEqualTo(defaultBrandDTO);
    }

    @Test
    void mapDTOToEntity() {
        when(modelMapper.map(defaultBrandDTO, Brand.class)).thenReturn(defaultBrandEntity);

        Brand brand = mapToEntity(defaultBrandDTO);

        assertThat(brand).usingRecursiveComparison().isEqualTo(defaultBrandEntity);
    }

    @Test
    void addBrand() {
        when(brandDao.save(any(Brand.class))).thenReturn(defaultBrandEntity);
        when(modelMapper.map(defaultBrandEntity, BrandDTO.class)).thenReturn(defaultBrandDTO);

        brandService.addBrand(defaultBrandCreateDTO);

        verify(brandDao, times(1)).save(any(Brand.class));
    }

    @Test
    void deleteBrand() {
        doNothing().when(brandDao).deleteById(1L);

        brandService.deleteBrand(1L);

        verify(brandDao, times(1)).deleteById(1L);
    }

    @Test
    void getAllBrands() {
        List<Brand> brands = List.of(defaultBrandEntity);
        when(brandDao.findAll()).thenReturn(brands);
        when(modelMapper.map(defaultBrandEntity, BrandDTO.class)).thenReturn(defaultBrandDTO);

        List<BrandDTO> brandDTOs = brandService.getAllBrands();

        assertThat(brandDTOs).usingRecursiveComparison().isEqualTo(List.of(defaultBrandDTO));
    }

    @Test
    void findBrandById_existingId() {
        when(brandDao.findById(1L)).thenReturn(Optional.of(defaultBrandEntity));

        Optional<Brand> brand = brandService.findBrandById(1L);

        assertThat(brand).isPresent();
        assertThat(brand.get()).usingRecursiveComparison().isEqualTo(defaultBrandEntity);
    }

    @Test
    void findBrandById_nonExistentId() {
        when(brandDao.findById(2L)).thenReturn(Optional.empty());

        Optional<Brand> brand = brandService.findBrandById(2L);

        assertThat(brand).isNotPresent();
    }

    @Test
    void findBrandByName_existingName() {
        when(brandDao.findByName("brandName")).thenReturn(Optional.of(defaultBrandEntity));

        Optional<Brand> brand = brandService.findBrandByName("brandName");

        assertThat(brand).isPresent();
        assertThat(brand.get()).usingRecursiveComparison().isEqualTo(defaultBrandEntity);
    }

    @Test
    void findBrandByName_nonExistentName() {
        when(brandDao.findByName("nonExistentName")).thenReturn(Optional.empty());

        Optional<Brand> brand = brandService.findBrandByName("nonExistentName");

        assertThat(brand).isNotPresent();
    }

    private Brand mapToEntity(BrandDTO brandDTO) {
        return modelMapper.map(brandDTO, Brand.class);
    }

    private BrandDTO mapToDTO(Brand brand) {
        return modelMapper.map(brand, BrandDTO.class);
    }
}
