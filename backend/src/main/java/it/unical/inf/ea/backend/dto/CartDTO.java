package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.data.entities.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    @NotNull
    private UUID id;

    @NotNull
    private List<CartItemDTO> items;
}
