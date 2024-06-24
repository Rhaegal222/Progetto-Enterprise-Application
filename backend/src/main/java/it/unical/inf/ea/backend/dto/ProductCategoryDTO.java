package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.data.entities.ProductCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductCategoryDTO  {

    @NotNull
    private Integer id;

    @NotNull
    @NotEmpty
    private String categoryName;

}
