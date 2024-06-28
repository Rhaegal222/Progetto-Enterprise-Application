package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.basics.PaymentMethodBasicDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentMethodService {

    PaymentMethodDTO createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException;
    PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
    void setDefaultPaymentMethod(String id) throws IllegalAccessException;
    void deletePaymentMethod(String id) throws IllegalAccessException;
    PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException;
    List<PaymentMethodDTO> getAllPaymentMethods();
}