package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;

import java.util.List;

public interface PaymentMethodService {

    void createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException;
    PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
    void setDefaultPaymentMethod(String id) throws IllegalAccessException;
    void deletePaymentMethod(String id) throws IllegalAccessException;
    PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException;
    List<PaymentMethodDTO> getAllPaymentMethods();
    List<PaymentMethodDTO> getAllLoggedUserPaymentMethods();
}
