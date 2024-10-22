package it.unical.inf.ea.backend.dto.basics;

import it.unical.inf.ea.backend.dto.UserImageDTO;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.validation.constraints.Email;
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

    @Email
    @NotNull
    private String email;

    private String phoneNumber;

    private UserImageDTO image;

    private UserStatus status;

    private Provider provider;

    private UserRole role;

}
