package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.Visibility;
import lombok.*;

import java.util.Set;
@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class WishListDTO {
    private Long id;
    private String WishListName;
    private String userId;
    private Visibility visibility;
    private String productId;
    private Set<ProductDTO> products;
}
