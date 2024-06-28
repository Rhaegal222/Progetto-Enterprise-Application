package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;

import java.util.List;

public interface AddressService {

    AddressDTO createAddress(AddressCreateDTO addressCreateDTO) throws IllegalAccessException;
    AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException;
    void setDefaultAddress(String id) throws IllegalAccessException;
    void deleteAddress(String id) throws IllegalAccessException;
    AddressDTO getAddressById(String id) throws IllegalAccessException;
    List<AddressDTO> getAllAddresses();
}
