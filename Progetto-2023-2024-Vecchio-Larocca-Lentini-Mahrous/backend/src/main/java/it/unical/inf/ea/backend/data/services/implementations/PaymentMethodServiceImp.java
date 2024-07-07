package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.PaymentMethodDao;
import it.unical.inf.ea.backend.data.entities.PaymentMethod;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.PaymentMethodService;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImp implements PaymentMethodService {

    private final PaymentMethodDao paymentMethodDao;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public void createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if (loggedUser == null) {
                throw new IllegalStateException("Logged user cannot be null");
            }

            PaymentMethod paymentMethod = modelMapper.map(paymentMethodCreateDTO, PaymentMethod.class);
            paymentMethod.setUser(loggedUser);

            if (paymentMethod.isDefault()) {
                for (PaymentMethod existingMethod : loggedUser.getPaymentMethods()) {
                    if (existingMethod.isDefault()) {
                        existingMethod.setDefault(false);
                        paymentMethodDao.save(existingMethod);
                    }
                }
            }
            paymentMethodDao.save(paymentMethod);
        } catch (Exception e) {
            throw new IllegalAccessException("Cannot create payment method");
        }
    }

    @Override
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO patch) throws IllegalAccessException {

        PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getUser().equals(loggedUser)) {
            throw new IllegalAccessException("User cannot update payment method");
        }

        paymentMethod.setCardNumber(patch.getCardNumber());

        paymentMethod.setExpireMonth(patch.getExpireMonth());
        paymentMethod.setExpireYear(patch.getExpireYear());

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
    @Transactional
    public void setDefaultPaymentMethod(UUID id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getUser().equals(loggedUser)) {
            throw new IllegalAccessException("User cannot set default payment method");
        }

        for (PaymentMethod existingMethod : loggedUser.getPaymentMethods()) {
            if (existingMethod.isDefault()) {
                existingMethod.setDefault(false);
                paymentMethodDao.save(existingMethod);
            }
        }

        paymentMethod.setDefault(true);
        paymentMethodDao.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(UUID id) throws IllegalAccessException {
        try{
            PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            if(loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getUser().equals(loggedUser))
                throw new IllegalAccessException("Cannot delete payment method");
            paymentMethodDao.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalAccessException("Cannot delete this payment method");
        }
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(UUID id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot get payment method");
        }

        return mapToDTO(paymentMethod);
    }

    @Override
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = paymentMethodDao.findAll();
        return paymentMethods.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodDTO> getAllLoggedUserPaymentMethods() {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        List<PaymentMethod> paymentMethods = paymentMethodDao.findAllByUserId(loggedUser.getId());
        return paymentMethods.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }
}
