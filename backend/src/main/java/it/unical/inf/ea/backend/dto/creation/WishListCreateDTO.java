package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.WishlistVisibility;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WishlistCreateDTO {

    @NonNull
    private String name;

    @NonNull
    private WishlistVisibility visibility;

}
