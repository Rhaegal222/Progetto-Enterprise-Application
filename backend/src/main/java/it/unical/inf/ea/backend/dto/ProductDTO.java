package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.data.entities.ProductImage;
import it.unical.inf.ea.backend.dto.enums.Availability;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class ProductDTO {

    @NotNull
    private String id;

    @Length(max = 100)
    @NotNull
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

    @NotNull
    private BrandDTO brand;

    private String productWeight;

    @NotNull
    private int quantity;

    @NotNull
    private Availability availability;

    @NotNull
    private ProductCategoryDTO productCategory;

    private ProductImageDTO photoProduct;

    @NotNull
    private boolean onSale;

    private BigDecimal discountedPrice;

}