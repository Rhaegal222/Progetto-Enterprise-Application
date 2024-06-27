package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.PaymentMethodDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.PaymentMethod;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.PaymentMethodService;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.basics.PaymentMethodBasicDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImp implements PaymentMethodService {

    private final PaymentMethodDao paymentMethodDao;
    private final UserDao userDao;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final JwtContextUtils jwtContextUtils;

    @Transactional
    public PaymentMethodDTO createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException {
        System.out.println("Creating payment method with DTO: {}");
        System.out.println(paymentMethodCreateDTO);

        PaymentMethod paymentMethod = modelMapper.map(paymentMethodCreateDTO, PaymentMethod.class);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser == null) {
            System.out.println("Logged user is null. Security context: {}");
            System.out.println(SecurityContextHolder.getContext());
            throw new IllegalStateException("Logged user cannot be null");
        }

        System.out.println("Logged user: {}");
        System.out.println(loggedUser);

        paymentMethod.setOwnerUser(loggedUser);

        if (paymentMethod.isDefault()) {
            for (PaymentMethod existingMethod : loggedUser.getPaymentMethods()) {
                if (existingMethod.isDefault()) {
                    existingMethod.setDefault(false);
                    paymentMethodDao.save(existingMethod);
                }
            }
        }

        paymentMethod = paymentMethodDao.save(paymentMethod);
        System.out.println("Payment method created: {}");
        System.out.println(paymentMethod);
        return mapToDTO(paymentMethod);
    }





    @Override
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id,patch);
        PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getOwnerUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot update payment method");
        }

        paymentMethod.setCreditCard(patch.getCreditCard());

        paymentMethod.setExpiryDate(patch.getExpiryDate());

        paymentMethod.setOwner(patch.getOwner());

        if(patch.getIsDefault() && !paymentMethod.isDefault()){
            for(PaymentMethod paymentMethod1:loggedUser.getPaymentMethods()) {
                if (paymentMethod1.isDefault()) {
                    paymentMethod1.setDefault(false);
                    paymentMethodDao.save(paymentMethod1);
                }
            }
            paymentMethod.setDefault(true);
        }
        else if(!patch.getIsDefault() && paymentMethod.isDefault() )
            paymentMethod.setDefault(false);

        paymentMethodDao.save(paymentMethod);
        return mapToDTO(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(String id) throws IllegalAccessException {
        try{
            PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if(loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getOwnerUser().getId().equals(loggedUser.getId()))
                throw new IllegalAccessException("Cannot delete payment method");
            paymentMethodDao.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(paymentMethod.getOwnerUser().getId())) {
            throw new IllegalAccessException("User cannot get payment method");
        }

        return mapToDTO(paymentMethod);
    }

    @Override
    public Page<PaymentMethodBasicDTO> getMyPaymentMethods(int page, int size) {
        User user = jwtContextUtils.getUserLoggedFromContext();
        Page<PaymentMethod> paymentMethods = new PageImpl<PaymentMethod>(user.getPaymentMethods(), PageRequest.of(page,size),user.getPaymentMethods().size());
        List<PaymentMethodBasicDTO> collect = paymentMethods.stream().map(s->modelMapper.map(s, PaymentMethodBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page,size),paymentMethods.getTotalElements());
    }

    public PaymentMethod mapToEntity(PaymentMethodDTO paymentMethodDTO) {
        return modelMapper.map(paymentMethodDTO, PaymentMethod.class);
    }
    public PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }

    private void throwOnIdMismatch(String id, PaymentMethodDTO paymentMethodDTO) {
        if (!paymentMethodDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}