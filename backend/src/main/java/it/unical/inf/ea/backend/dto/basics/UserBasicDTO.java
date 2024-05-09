package it.unical.inf.ea.backend.dto.basics;

import it.unical.inf.ea.backend.dto.UserImageDTO;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserBasicDTO {

    @NotNull
    private String id;

    @Length(min = 3, max = 25)
    @NotNull
    private String username;

    @Length(max = 500)
    private String bio;

    private UserImageDTO photoProfile;
    private Integer reviewsTotalSum;
    private Integer reviewsNumber;

    private Integer followersNumber;
    private Integer followingNumber;

    private UserStatus status;

}
