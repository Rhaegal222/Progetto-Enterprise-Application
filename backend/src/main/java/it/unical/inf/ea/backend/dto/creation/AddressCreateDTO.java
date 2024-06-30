package it.unical.inf.ea.backend.dto.creation;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class AddressCreateDTO {

    @NotNull
    @Length(max = 100)
    private String firstName;

    @NotNull
    @Length(max = 100)
    private String lastName;

    @NotNull
    @Length(max = 100)
    private String country;

    @NotNull
    @Length(max = 100)
    private String city;

    @NotNull
    @Length(max = 100)
    private String street;

    @NotNull
    @Length(max = 10)
    private String zipCode;

    @NotNull
    private Boolean isDefault;

}
