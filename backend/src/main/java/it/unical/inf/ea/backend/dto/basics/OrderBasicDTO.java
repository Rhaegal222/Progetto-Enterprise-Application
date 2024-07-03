package it.unical.inf.ea.backend.dto.basics;

import it.unical.inf.ea.backend.dto.CartItemDTO;
import it.unical.inf.ea.backend.dto.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderBasicDTO {

    @NotNull
    private String id;

    @NotNull
    private List<CartItemDTO> items;

    @NotNull
    private OrderStatus status;

}
