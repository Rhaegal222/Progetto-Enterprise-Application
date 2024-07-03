package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.enums.WishlistVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WishlistCreateDTO {

    @NonNull
    private String wishlistName;

    @NonNull
    private WishlistVisibility wishlistVisibility;

    private List<ProductDTO> products;

}
