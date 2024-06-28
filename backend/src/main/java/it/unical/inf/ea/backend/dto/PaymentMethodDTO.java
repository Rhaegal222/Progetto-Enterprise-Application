package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentMethodDTO {

    private String id;

    @NotNull
    @Length(min = 19, max = 19)
    private String cardNumber;

    @NotNull
    private String expireMonth;

    @NotNull
    private String expireYear;

    @NotNull
    private Boolean isDefault;

    @NotNull
    @Length(max = 25)
    private String owner;
}