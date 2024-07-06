package it.unical.inf.ea.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class SharedWishlistAccessDTO {
    @NonNull
    private Long id;

    private WishlistDTO wishlist;

    private UserDTO user;
}
