package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    @NotNull
    private String id;

    @NotNull
    private Long productId;

    @NotNull
    private int quantity;
}
