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
    private Long userId;
    private Visibility visibility;
    private Set<ProductDTO> products;
}
