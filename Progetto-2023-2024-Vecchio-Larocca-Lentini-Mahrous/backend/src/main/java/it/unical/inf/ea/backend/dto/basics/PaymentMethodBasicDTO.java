package it.unical.inf.ea.backend.dto.basics;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PaymentMethodBasicDTO {

    @NotNull
    private String id;

    @NotNull
    private boolean isDefault;
}
