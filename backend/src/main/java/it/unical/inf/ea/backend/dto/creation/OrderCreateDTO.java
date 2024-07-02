package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderCreateDTO {
    @NotNull
    private CartDTO cart;

    @NotNull
    private AddressDTO deliveryAddress;

    @NotNull
    private PaymentMethodDTO paymentMethod;
}

