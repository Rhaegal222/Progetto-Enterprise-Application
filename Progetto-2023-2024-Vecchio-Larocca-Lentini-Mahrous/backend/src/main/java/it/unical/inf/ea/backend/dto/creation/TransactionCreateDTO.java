package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.basics.OrderBasicDTO;
import it.unical.inf.ea.backend.dto.basics.PaymentMethodBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class TransactionCreateDTO {
    @NotNull
    private PaymentMethodBasicDTO paymentMethod;

    @NotNull
    private OrderBasicDTO order;
}
