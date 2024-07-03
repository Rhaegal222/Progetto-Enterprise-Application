package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.AddressDao;
import it.unical.inf.ea.backend.data.entities.Address;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.AddressService;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImp implements AddressService {

    private final AddressDao addressDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public AddressDTO createAddress(AddressCreateDTO addressCreateDTO) throws IllegalAccessException {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            Address address = modelMapper.map(addressCreateDTO, Address.class);
            address.setUser(loggedUser);

            if (address.getIsDefault()) {
                for (Address existingAddress : loggedUser.getAddresses()) {
                    if (existingAddress.getIsDefault()) {
                        existingAddress.setIsDefault(false);
                        addressDao.save(existingAddress);
                    }
                }
            }

            address = addressDao.save(address);

            return mapToDTO(address);
        } catch (Exception e) {
            throw new IllegalAccessException("Cannot create address");
        }
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(String id, AddressDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !address.getUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot update address");
        }

        address.setFullName(patch.getFullName());
        address.setCountry(patch.getCountry());
        address.setCity(patch.getCity());
        address.setStreet(patch.getStreet());
        address.setZipCode(patch.getZipCode());

        if (patch.getIsDefault() && !address.getIsDefault()) {
            for (Address existingAddress : loggedUser.getAddresses()) {
                if (existingAddress.getIsDefault()) {
                    existingAddress.setIsDefault(false);
                    addressDao.save(existingAddress);
                }
            }
            address.setIsDefault(true);
        } else if (!patch.getIsDefault() && address.getIsDefault()) {
            address.setIsDefault(false);
        }

        addressDao.save(address);
        return mapToDTO(address);
    }

    @Override
    public void setDefaultAddress(String id) throws IllegalAccessException {
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !address.getUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot set default address");
        }

        for (Address existingAddress : loggedUser.getAddresses()) {
            if (existingAddress.getIsDefault()) {
                existingAddress.setIsDefault(false);
                addressDao.save(existingAddress);
            }
        }

        address.setIsDefault(true);
        addressDao.save(address);
    }

    @Override
    public void deleteAddress(String id) throws IllegalAccessException {
        try {
            Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser.getRole().equals(UserRole.USER) && !address.getUser().getId().equals(loggedUser.getId())) {
                throw new IllegalAccessException("Cannot delete address");
            }
            addressDao.deleteById(id);
        } catch (Exception e) {
            throw new IllegalAccessException("Cannot delete address");
        }
    }

    @Override
    public AddressDTO getAddressById(String id) throws IllegalAccessException {
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !address.getUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot get address");
        }

        return mapToDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressDao.findAll();
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AddressDTO> getAllLoggedUserShippingAddresses() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        List<Address> addresses = addressDao.findAllByUserId(loggedUser.getId());
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private void throwOnIdMismatch(String id, AddressDTO patch) {
        if (patch.getId() == null) {
            patch.setId(id);
        } else if (!id.equals(patch.getId())) {
            throw new IdMismatchException();
        }
    }

    private AddressDTO mapToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }
}
