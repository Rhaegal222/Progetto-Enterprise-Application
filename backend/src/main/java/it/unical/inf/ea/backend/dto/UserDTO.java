package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotNull
    private String id;

    @Length(min = 3, max = 25)

    @NotNull
    private String username;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String cf;

    @Email
    @NotNull
    private String email;

    private UserImageDTO photoProfile;

    @NotNull
    private Provider provider;

    @NotNull
    private UserStatus status;

    @NotNull
    private String password;

    private List<AddressDTO> addresses;

    private List<PaymentMethodDTO> paymentMethods;

    @NotNull
    private UserRole role;

}
