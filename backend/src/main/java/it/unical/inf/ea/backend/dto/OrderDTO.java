package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderDTO {

    @NotNull
    private String id;

    @NotNull
    private CartDTO cart;

    @NotNull
    private UserBasicDTO user;

    private AddressDTO deliveryAddress;

    private PaymentMethodDTO paymentMethod;
}
