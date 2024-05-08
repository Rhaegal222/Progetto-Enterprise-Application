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
    private OrderState state;

    @Past
    private LocalDateTime orderDate;

    @Past
    private LocalDateTime orderUpdateDate;

    @NotNull
    private ProductBasicDTO product;

    @NotNull
    private UserBasicDTO user;


    private DeliveryDTO delivery;

    private AddressDTO deliveryAddress;

    private TransactionDTO transaction;
}
