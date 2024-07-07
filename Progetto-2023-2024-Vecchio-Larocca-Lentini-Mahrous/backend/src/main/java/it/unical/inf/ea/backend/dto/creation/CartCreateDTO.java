package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.CartItemDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateDTO {

    @NotNull
    private List<CartItemDTO> items;

}
