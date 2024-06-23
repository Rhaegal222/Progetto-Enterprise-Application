package it.unical.inf.ea.backend.dto.creation;


import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.enums.Availability;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
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
    private BigDecimal productPrice;

    @NotNull
    private BigDecimal deliveryPrice;

    @Length(max = 1000)
    private String productWeight;

    @NotNull
    private Availability availability;

    @NotNull
    private int quantity;

    @NotNull
    private BrandDTO brand;

    @NotNull
    private ProductCategoryDTO productCategory;
}

