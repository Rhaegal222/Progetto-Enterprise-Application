package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    @NotNull
    private String id;

    @NotNull
    private String productId;

    @NotNull
    private int quantity;
}
