package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.Visibility;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class WishListCreateDTO {

    private Long userId;
    private Visibility visibility;
}
