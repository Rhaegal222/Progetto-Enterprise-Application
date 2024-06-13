package it.unical.inf.ea.backend.dto.creation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({ //annotazione che definisce i sotto-tipi per la classe genitore
        @JsonSubTypes.Type(value = SavoryCreateDTO.class, name = "Savory"),
        @JsonSubTypes.Type(value = SweetCreateDTO.class, name = "Sweet"),
        @JsonSubTypes.Type(value = AlcoholicCreateDTO.class, name = "Alcoholic")
})
public class ProductCreateDTO {

    @NotNull
    @Length(max = 100)
    private String title;

    @Length(max = 1000)
    private String description;

    @Length(max = 1000)
    private String ingredients;

    @Length(max = 1000)
    private String nutritionalValues;

    @NotNull
    @PositiveOrZero
    private BigDecimal productPrice;

    @NotNull
    @PositiveOrZero
    private BigDecimal deliveryPrice;

    @Length(max = 100)
    private String brand;

    @NotNull
    private ProductSize productSize;

    @NotNull
    private Availability availability;

    @NotNull
    private ProductCategoryDTO productCategory;
}

