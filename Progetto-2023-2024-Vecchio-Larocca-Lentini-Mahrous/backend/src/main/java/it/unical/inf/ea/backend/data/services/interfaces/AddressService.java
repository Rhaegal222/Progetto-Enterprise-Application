package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressDTO createAddress(AddressCreateDTO addressCreateDTO) throws IllegalAccessException;
    AddressDTO updateAddress(UUID id, AddressDTO addressDTO) throws IllegalAccessException;
    void setDefaultAddress(UUID id) throws IllegalAccessException;
    void deleteAddress(UUID id) throws IllegalAccessException;
    AddressDTO getAddressById(UUID id) throws IllegalAccessException;
    List<AddressDTO> getAllAddresses();
    List<AddressDTO> getAllLoggedUserAddresses();
}
