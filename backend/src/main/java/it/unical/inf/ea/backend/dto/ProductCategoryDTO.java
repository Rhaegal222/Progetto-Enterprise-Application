package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductCategoryDTO {

    private String id;

    @NotNull
    private String CategoryName;

}
