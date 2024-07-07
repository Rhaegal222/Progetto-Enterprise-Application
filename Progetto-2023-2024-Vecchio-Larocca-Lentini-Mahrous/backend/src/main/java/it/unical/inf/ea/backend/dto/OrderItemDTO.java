package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderItemDTO {

    @NotNull
    private UUID id;

    @NotNull
    private Long productId;

    @NotNull
    private String productName;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal partialCost;
}
