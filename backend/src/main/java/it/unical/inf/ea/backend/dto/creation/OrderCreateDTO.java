package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.AddressDTO;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.CartItemDTO;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderCreateDTO {

    @NotNull
    private List<CartItemDTO> items;

    @NotNull
    private String addressId;

    @NotNull
    private String paymentMethodId;

    @NotNull
    private String userId;

}

