package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.dto.enums.Availability;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)

public class ProductDTO {

    @NotNull
    private String id;

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
    private BigDecimal productPrice;

    @NotNull
    private BigDecimal deliveryPrice;

    @Length(max = 100)
    private String brand;

    private String productWeight;

    @NotNull
    private int quantity;

    @NotNull
    private Availability availability;

    @NotNull
    private ProductCategoryDTO productCategory;

    private List<ProductImageDTO> productImages;

}