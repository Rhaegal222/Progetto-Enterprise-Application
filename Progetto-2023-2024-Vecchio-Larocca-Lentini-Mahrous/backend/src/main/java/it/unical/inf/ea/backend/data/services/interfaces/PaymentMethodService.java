package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodService {

    void createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException;
    PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
    void setDefaultPaymentMethod(UUID id) throws IllegalAccessException;
    void deletePaymentMethod(UUID id) throws IllegalAccessException;
    PaymentMethodDTO getPaymentMethodById(UUID id) throws IllegalAccessException;
    List<PaymentMethodDTO> getAllPaymentMethods();
    List<PaymentMethodDTO> getAllLoggedUserPaymentMethods();
}
