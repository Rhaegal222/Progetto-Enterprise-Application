package it.unical.inf.ea.backend.dto.creation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

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
    private String descriptionBrand;

    @Length(max = 1000)
    private String ingredients;

    @Length(max = 1000)
    private String nutritionalValues;

    @NotNull
    private CustomMoneyDTO productCost;

    @NotNull
    private CustomMoneyDTO deliveryCost;

    @Length(max = 100)
    private String brand;

    @NotNull
    private ProductCategoryDTO productCategory;
}

