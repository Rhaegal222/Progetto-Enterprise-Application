package it.unical.inf.ea.backend.dto.basics;

import com.fasterxml.jackson.annotation.JsonSetter;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class ProductBasicDTO {

    @NotNull
    private String id;

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

    @Past
    private LocalDateTime uploadDate;

    @NotNull
    private BigDecimal productCost;

    @NotNull
    private BigDecimal deliveryCost;

    @Length(max = 100)
    private String brand;

    private List<ProductImageDTO> productImages;
    private ProductCategoryDTO productCategory;

    @JsonSetter
    public void setProductImages(List<ProductImageDTO> productImages) {
        if(productImages != null && productImages.size() > 0)
            this.productImages = List.of(productImages.get(0));
    }

}
