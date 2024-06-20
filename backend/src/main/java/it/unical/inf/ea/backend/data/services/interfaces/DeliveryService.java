package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.DeliveryDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import it.unical.inf.ea.backend.dto.creation.DeliveryCreateDTO;
import org.springframework.stereotype.Service;

public interface DeliveryService {

    DeliveryDTO createDelivery(DeliveryCreateDTO deliveryCreateDTO) throws IllegalAccessException;
    DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    DeliveryDTO getDeliveryById(String id) throws IllegalAccessException;

    AddressDTO createAddress(AddressCreateDTO addressCreateDTO);
    AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException;
    void deleteAddress(String id);
    AddressDTO getAddress(String id);
    Iterable<AddressDTO> getMyAddressList() throws IllegalAccessException;
}
