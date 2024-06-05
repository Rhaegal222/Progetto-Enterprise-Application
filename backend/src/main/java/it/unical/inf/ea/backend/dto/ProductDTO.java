package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({ //annotazione che definisce i sotto-tipi per la classe genitore
        @JsonSubTypes.Type(value = SavoryDTO.class, name = "Savory"),
        @JsonSubTypes.Type(value = SweetDTO.class, name = "Sweet"),
        @JsonSubTypes.Type(value = AlcoholicDTO.class, name = "Alcoholic")
})
public class ProductDTO {

    @NotNull
    private Long id;

    @Length(max = 100)
    @NotNull
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
    private ProductSize productSize;

    @NotNull
    private Availability availability;

    @NotNull
    private ProductCategoryDTO productCategory;

    private List<ProductImageDTO> productImages;

}