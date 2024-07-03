package it.unical.inf.ea.backend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderDTO {

    @NotNull
    private String id;

    @NotNull
    private List<CartItemDTO> items;

    @NotNull
    private String addressId;

    @NotNull
    private String paymentMethodId;

    @NotNull
    private String userId;
}
