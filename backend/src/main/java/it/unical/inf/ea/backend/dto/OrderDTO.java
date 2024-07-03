package it.unical.inf.ea.backend.dto;


import it.unical.inf.ea.backend.dto.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.cglib.core.Local;

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
    private OrderStatus status;

    @NotNull
    private Local createdAt;

    @NotNull
    private Local updatedAt;
}
