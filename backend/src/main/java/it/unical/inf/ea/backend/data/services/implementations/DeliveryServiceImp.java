package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.AddressDao;
import it.unical.inf.ea.backend.data.dao.DeliveryDao;
import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.Address;
import it.unical.inf.ea.backend.data.entities.Delivery;
import it.unical.inf.ea.backend.data.entities.Order;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.DeliveryService;
import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.DeliveryDTO;
import it.unical.inf.ea.backend.dto.creation.AddressCreateDTO;
import it.unical.inf.ea.backend.dto.creation.DeliveryCreateDTO;
import it.unical.inf.ea.backend.dto.enums.DeliveryStatus;
import it.unical.inf.ea.backend.dto.enums.TransactionState;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {

    private final DeliveryDao deliveryDao;
    private final OrderDao orderDao;
    //private final JwtContextUtils jwtContextUtils;
    private final SalesExecutionServiceImp salesExecutionServiceImp;
    private final ModelMapper modelMapper; //Converte un DTO in un modello di dominio
    private final Clock clock;
    private final AddressDao addressDao;

    @Override
    public DeliveryDTO createDelivery(DeliveryCreateDTO deliveryDTO) throws IllegalAccessException{
        Order order = orderDao.findById(Long.valueOf(deliveryDTO.getOrder().getId())).orElseThrow(EntityNotFoundException::new);

        //User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(order.getTransaction()!=null && !order.getTransaction().getTransactionState().equals(TransactionState.COMPLETED))
            throw new IllegalAccessException("Transaction completion needed for delivery.");

        if(order.getTransaction() == null)
            throw new IllegalAccessException("Creating the delivery requires a transaction.");

        Address senderAddress = addressDao.findById(deliveryDTO.getSenderAddressId()).orElseThrow(EntityNotFoundException::new);

        //return mapToDTO(salesExecutionServiceImp.sendProduct(order,loggedUser,deliveryDTO.getShipper(), senderAddress))

        return null; //return temporaneo messo sennò segna l'errore (in attesa di loggedUser)
    }

    @Override
    public DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException {
        return null;
    }

    @Override
    public DeliveryDTO getDeliveryById(String id) throws IllegalAccessException {
        Delivery delivery = deliveryDao.findById(id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(order.getProduct().getSeller().getId()) || loggedUser.getId().equals(order.getUser().getId()) ))
            return mapToDTO(delivery);
        else if(!loggedUser.getRole().equals(UserRole.USER))
            return mapToDTO(delivery);
        else
            throw new IllegalAccessException();
         */
        return null; //da togliere
    }


    @Override
    public AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException {
        if(addressDTO!=null && !id.equals(addressDTO.getId()))
            throw new IdMismatchException();
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!address.getUser().equals(loggedUser))
            throw new IllegalAccessException("Cannot modify address of other user");

        assert addressDTO != null;
        address.setHeader(addressDTO.getHeader());
        address.setCountry(addressDTO.getCountry());
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setZipCode(addressDTO.getZipCode());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        if(addressDTO.getIsDefault() && !address.isDefault()){
            for(Address address1: loggedUser.getAddresses()){
                if(address1.isDefault() && !address1.equals(address)){
                    address1.setDefault(false);
                    addressDao.save(address1);
                }
            }
            address.setDefault(true);
        }
        else if(!addressDTO.getIsDefault() && address.isDefault())
            address.setDefault(false);
         */


        addressDao.save(address);


        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public void deleteAddress(String id) {
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !address.getUser().equals(loggedUser))
            throw new IllegalAccessException("Cannot delete address of other user");
        if (address.getOrders() != null && !address.getOrders().isEmpty())
            throw new IllegalArgumentException("Cannot delete address with orders");

        if (address.isDefault()){
            for (Address address1: loggedUser.getAddresses()){
                if(!address1.equals(address)){
                    address1.setDefault(true);
                    addressDao.save(address1);
                    break;
                }
            }
        }
         */
        addressDao.delete(address);
    }

    @Override
    public AddressDTO getAddress(String id) {
        Address address = addressDao.findById(id).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public Iterable<AddressDTO> getMyAddressList() throws IllegalAccessException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        return loggedUser.getAddresses().stream().map(s->modelMapper.map(s,AddressDTO.class)).collect(Collectors.toList());
         */
        return null;
    }

    @Override
    @Transactional
    public AddressDTO createAddress(AddressCreateDTO addressCreateDTO) {
        //User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        Address address = modelMapper.map(addressCreateDTO,Address.class);
        /*
        address.setUser(loggedUser);
        if(addressCreateDTO.getIsDefault()){
            for (Address address1: loggedUser.getAddresses()){
                if(address1.isDefault()){
                    address1.setDefault(false);
                    addressDao.save(address1);
                }
            }
        }
         */
        addressDao.save(address);
        return modelMapper.map(address,AddressDTO.class);
    }


    public DeliveryDTO mapToDTO(Delivery delivery) {
        return modelMapper.map(delivery, DeliveryDTO.class);
    }

    private void throwOnIdMismatch(String id, DeliveryDTO deliveryDTO) {
        if (!deliveryDTO.getId().equals(id))
            throw new IdMismatchException();
    }

}
