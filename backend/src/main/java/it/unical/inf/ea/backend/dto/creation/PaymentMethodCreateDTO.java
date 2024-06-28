package it.unical.inf.ea.backend.dto.creation;

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
public class PaymentMethodCreateDTO {

    @NotNull
    @Length(min = 16, max = 16)
    private String cardNumber;

    @NotNull
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    //private LocalDate expiryDate;
    private String expiryDate;

    @NotNull
    private Boolean isDefault;

    @NotNull
    @Length(max = 25)
    private String owner;

}