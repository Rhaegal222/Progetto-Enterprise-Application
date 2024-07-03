package it.unical.inf.ea.backend.dto.creation;


import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.enums.ProductAvailability;
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

    @Length(max = 100)
    @NotNull
    private String name;

    @Length(max = 1000)
    private String description;

    @Length(max = 1000)
    private String ingredients;

    @Length(max = 1000)
    private String nutritionalValues;

    @NotNull
    private String weight;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal shippingCost;

    @NotNull
    private ProductAvailability productAvailability;

    @NotNull
    private BrandDTO brand;

    @NotNull
    private CategoryDTO category;

    private ProductImageDTO image;

    @NotNull
    private boolean onSale;

    private BigDecimal discountedPrice;
}

