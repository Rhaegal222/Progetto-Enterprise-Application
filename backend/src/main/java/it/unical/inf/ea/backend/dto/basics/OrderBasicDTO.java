package it.unical.inf.ea.backend.dto.basics;

import it.unical.inf.ea.backend.dto.enums.OrderState;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderBasicDTO {

    @NotNull
    private String id;

    @NotNull
    private OrderState state;

    @Past
    private LocalDateTime orderDate;

    @Past
    private LocalDateTime orderUpdateDate;

    @NotNull
    private ProductBasicDTO product;

}
