package it.unical.inf.ea.backend.dto.basics;

import com.fasterxml.jackson.annotation.JsonSetter;
import it.unical.inf.ea.backend.dto.CategoryDTO;
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

    @NotNull
    private BigDecimal price;

    @Length(max = 100)
    private String brand;

    private List<ProductImageDTO> productImages;

    private CategoryDTO category;

    @JsonSetter
    public void setProductImages(List<ProductImageDTO> productImages) {
        if(productImages != null && productImages.size() > 0)
            this.productImages = List.of(productImages.get(0));
    }

    @NotNull
    private boolean onSale;

    private BigDecimal salePrice;

}
