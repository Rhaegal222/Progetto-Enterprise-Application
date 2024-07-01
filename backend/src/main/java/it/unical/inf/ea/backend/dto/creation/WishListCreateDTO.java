package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WishlistCreateDTO {

    @NotNull
    private String wishlistName;

    @NotNull
    private Visibility visibility;


}
