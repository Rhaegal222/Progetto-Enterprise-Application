package it.unical.inf.ea.backend.dto.creation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateDTO {

    @NotNull
    private String productId;

    @NotNull
    private int quantity;
}
