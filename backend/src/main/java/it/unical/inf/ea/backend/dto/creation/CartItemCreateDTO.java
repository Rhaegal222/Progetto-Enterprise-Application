package it.unical.inf.ea.backend.dto.creation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemCreateDTO {

    @NotNull
    private String productId;

    @NotNull
    private int quantity;

}
