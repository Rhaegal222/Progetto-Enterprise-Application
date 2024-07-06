package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.AddressDao;
import it.unical.inf.ea.backend.data.entities.Address;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.implementations.AddressServiceImp;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AddressServiceImpTest {

    @Mock
    private AddressDao addressDao;
    @Mock
    private JwtContextUtils jwtContextUtils;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImp addressServiceImp;
    private AddressCreateDTO defaultAddressCreateDTO;
    private AddressDTO defaultAddressDTO;
    private Address defaultAddressEntity;
    private User defaultUserEntity;

    @BeforeEach
    public void setUp() {
        defaultUserEntity = User.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .role(UserRole.USER)
                .build();

        defaultAddressCreateDTO = AddressCreateDTO.builder()
                .fullName("John Doe")
                .phoneNumber("1234567890")
                .street("Main Street")
                .additionalInfo("Apt 101")
                .postalCode("12345")
                .city("CityName")
                .province("ProvinceName")
                .country("Italy")
                .isDefault(true)
                .build();

        defaultAddressEntity = Address.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .fullName("John Doe")
                .phoneNumber("1234567890")
                .street("Main Street")
                .additionalInfo("Apt 101")
                .postalCode("12345")
                .city("CityName")
                .province("ProvinceName")
                .country("Italy")
                .isDefault(true)
                .user(defaultUserEntity)
                .build();

        defaultAddressDTO = AddressDTO.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .fullName("John Doe")
                .phoneNumber("1234567890")
                .street("Main Street")
                .additionalInfo("Apt 101")
                .postalCode("12345")
                .city("CityName")
                .province("ProvinceName")
                .country("Italy")
                .isDefault(true)
                .build();
    }

    @Test
    void mapEntityToDTO() {
        when(modelMapper.map(defaultAddressDTO, Address.class)).thenReturn(defaultAddressEntity);

        Address address = mapToEntity(defaultAddressDTO);

        assertThat(address).usingRecursiveComparison().isEqualTo(defaultAddressEntity);
    }

    @Test
    void mapDTOToEntity() {
        when(modelMapper.map(defaultAddressEntity, AddressDTO.class)).thenReturn(defaultAddressDTO);

        AddressDTO addressDTO = mapToDTO(defaultAddressEntity);

        assertThat(addressDTO).usingRecursiveComparison().isEqualTo(defaultAddressDTO);
    }

    @Test
    void saveAddress() throws IllegalAccessException {
        defaultUserEntity.setAddresses(new ArrayList<>());

        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(modelMapper.map(defaultAddressCreateDTO, Address.class)).thenReturn(defaultAddressEntity);
        when(addressDao.save(any(Address.class))).thenReturn(defaultAddressEntity);

        addressServiceImp.createAddress(defaultAddressCreateDTO);

        verify(addressDao, times(1)).save(eq(defaultAddressEntity));
    }

    @Test
    void updateAddress_nonExistentId() {
        UUID nonExistentId = UUID.fromString("00000002-0002-0002-0002-000000000002");

        AddressDTO addressDTO = AddressDTO.builder()
                .id(nonExistentId)  // Assicurati che l'ID nel DTO corrisponda a quello non esistente
                .fullName("John Doe")
                .phoneNumber("123456789")
                .street("123 Main St")
                .postalCode("12345")
                .city("City")
                .province("Province")
                .country("Country")
                .isDefault(true)
                .build();

        assertThrows(EntityNotFoundException.class, () -> {
            addressServiceImp.updateAddress(nonExistentId, addressDTO);
        });
    }


    @Test
    void replaceAddress() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(addressDao.findById(UUID.fromString("00000001-0001-0001-0001-000000000001"))).thenReturn(Optional.ofNullable(defaultAddressEntity));
        when(modelMapper.map(defaultAddressEntity, AddressDTO.class)).thenReturn(defaultAddressDTO);

        addressServiceImp.updateAddress(UUID.fromString("00000001-0001-0001-0001-000000000001"), defaultAddressDTO);

        verify(addressDao, times(1)).save(defaultAddressEntity);
    }

    @Test
    void deleteAddress() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(addressDao.findById(UUID.fromString("00000001-0001-0001-0001-000000000001"))).thenReturn(Optional.ofNullable(defaultAddressEntity));

        addressServiceImp.deleteAddress(UUID.fromString("00000001-0001-0001-0001-000000000001"));

        verify(addressDao, times(1)).deleteById(UUID.fromString("00000001-0001-0001-0001-000000000001"));
    }

    @Test
    void getAddressById() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(addressDao.findById(UUID.fromString("00000001-0001-0001-0001-000000000001"))).thenReturn(Optional.ofNullable(defaultAddressEntity));
        when(modelMapper.map(defaultAddressEntity, AddressDTO.class)).thenReturn(defaultAddressDTO);

        AddressDTO addressDTO = addressServiceImp.getAddressById(UUID.fromString("00000001-0001-0001-0001-000000000001"));

        assertThat(addressDTO).usingRecursiveComparison().isEqualTo(defaultAddressDTO);
    }

    @Test
    void getAllAddresses() {
        List<Address> addresses = List.of(defaultAddressEntity);
        when(addressDao.findAll()).thenReturn(addresses);
        when(modelMapper.map(defaultAddressEntity, AddressDTO.class)).thenReturn(defaultAddressDTO);

        List<AddressDTO> addressDTOs = addressServiceImp.getAllAddresses();

        assertThat(addressDTOs).usingRecursiveComparison().isEqualTo(List.of(defaultAddressDTO));
    }

    private Address mapToEntity(AddressDTO addressDTO) {
        return modelMapper.map(addressDTO, Address.class);
    }

    private AddressDTO mapToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }
}
