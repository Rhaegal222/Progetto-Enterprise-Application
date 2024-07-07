package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.WishlistVisibility;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class WishlistDTO {

    @NonNull
    private String id;

    @NonNull
    private String wishlistName;

    @NonNull
    private WishlistVisibility visibility;

    private List<ProductDTO> products;
}
