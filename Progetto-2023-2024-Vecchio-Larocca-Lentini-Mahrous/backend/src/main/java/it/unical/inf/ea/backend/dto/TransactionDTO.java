package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.TransactionState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionDTO {

    @NotNull
    private String id;

    @Past
    private LocalDateTime creationTime;

    @Min(0)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionState transactionState;

    @NotNull
    private String paymentMethod;

    @NotNull
    private String paymentMethodOwner;

}
