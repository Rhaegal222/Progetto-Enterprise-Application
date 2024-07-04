package it.unical.inf.ea.backend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderDTO {

    @NotNull
    private UUID id;

    @NotNull
    private List<OrderItemDTO> items;

    @NotNull
    private BigDecimal totalCost;

    @NotNull
    private String status;

    @NotNull
    private String createdAt;

    @NotNull
    private String updatedAt;

    @NotNull
    private UserDTO user;

    @NotNull
    private AddressDTO address;

    @NotNull
    private PaymentMethodDTO paymentMethod;
}
