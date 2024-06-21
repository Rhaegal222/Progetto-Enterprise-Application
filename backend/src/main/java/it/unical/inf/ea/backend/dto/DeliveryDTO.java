package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeliveryDTO {
    @NotNull
    private String Id;

    @NotNull
    private LocalDateTime sendTime;


    private LocalDateTime deliveredTime;


    private BigDecimal deliveryCost;

    @Length(max = 50)
    private String shipper;

    @NotNull
    private DeliveryStatus deliveryStatus;

    @NotNull
    private AddressDTO senderAddress;

    @NotNull
    private AddressDTO receiverAddress;
}
