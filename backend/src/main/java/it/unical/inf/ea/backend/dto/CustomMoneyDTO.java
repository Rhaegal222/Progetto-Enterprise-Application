package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomMoneyDTO {

    @NotNull
    @PositiveOrZero
    private Double price;

    @NotNull
    private Currency currency;
}
