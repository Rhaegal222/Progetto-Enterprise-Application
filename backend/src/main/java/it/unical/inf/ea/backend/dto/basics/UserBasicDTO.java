package it.unical.inf.ea.backend.dto.basics;

import it.unical.inf.ea.backend.dto.UserImageDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
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

    @NotNull
    private String lastName;

    @NotNull
    private String firstName;

    private UserImageDTO photoProfile;

    private UserStatus status;

    private UserRole role;

}
