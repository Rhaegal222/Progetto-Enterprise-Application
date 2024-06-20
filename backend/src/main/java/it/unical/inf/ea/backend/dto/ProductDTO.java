package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class ProductDTO {

    @NotNull
    private Long id;

    @Length(max = 100)
    @NotNull
    @NotEmpty
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
    private Integer likesNumber ;

    @NotNull
    private ProductCategoryDTO productCategory;

    private List<ProductImageDTO> productImages;

}