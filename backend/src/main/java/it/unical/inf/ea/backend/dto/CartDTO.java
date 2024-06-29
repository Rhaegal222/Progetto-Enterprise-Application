package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    @NotNull
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private List<CartItemDTO> cartItems;
}
