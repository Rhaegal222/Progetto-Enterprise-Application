package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@ToString
public class UserDTO {

    private String id;


    @NotNull
    private String username;

    @NotNull
    private String password;

    @Email
    @NotNull
    private String email;

    @NotNull
    private Provider provider;

    @NotNull
    private boolean emailVerified;

    @NotNull
    private UserStatus status;

    @NotNull
    private UserRole role;

}
