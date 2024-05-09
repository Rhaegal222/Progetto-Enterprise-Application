package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Email
    @NotNull
    private String email;

    private UserImageDTO photoProfile;

    @NotNull
    private Provider provider;

    @NotNull
    private UserStatus status;

    private List<AddressDTO> addresses;

    private List<PaymentMethodDTO> paymentMethods;

    @NotNull
    private UserRole role;

}
