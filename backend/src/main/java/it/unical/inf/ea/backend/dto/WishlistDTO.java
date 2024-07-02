package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import lombok.*;

import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class WishlistDTO {

    @NonNull
    private Long id;

    @NonNull
    private String wishlistName;

    @NonNull
    private String  userId;

    @NonNull
    private Visibility visibility;

    private List<ProductDTO> products;
}
