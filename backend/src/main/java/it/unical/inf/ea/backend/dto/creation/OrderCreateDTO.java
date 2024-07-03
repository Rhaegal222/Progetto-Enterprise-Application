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
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderCreateDTO {

    @NotNull
    private List<CartItemDTO> items;

    @NotNull
    private UUID addressId;

    @NotNull
    private UUID paymentMethodId;

    @NotNull
    private UUID userId;

}

