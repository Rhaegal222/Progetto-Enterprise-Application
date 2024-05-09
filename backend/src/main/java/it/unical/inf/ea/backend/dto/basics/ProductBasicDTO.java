package it.unical.inf.ea.backend.dto.basics;

import com.fasterxml.jackson.annotation.JsonSetter;
import it.unical.inf.ea.backend.dto.CustomMoneyDTO;
import it.unical.inf.ea.backend.dto.ProductCategoryDTO;
import it.unical.inf.ea.backend.dto.ProductImageDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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

    @Past
    private LocalDateTime uploadDate;

    @NotNull
    private CustomMoneyDTO productCost;

    @NotNull
    private CustomMoneyDTO deliveryCost;

    @Length(max = 100)
    private String brand;


    private Integer likesNumber;
    private UserBasicDTO seller;
    private List<ProductImageDTO> productImages;
    private ProductCategoryDTO productCategory;

    @JsonSetter
    public void setProductImages(List<ProductImageDTO> productImages) {
        if(productImages != null && productImages.size() > 0)
            this.productImages = List.of(productImages.get(0));
    }

}
