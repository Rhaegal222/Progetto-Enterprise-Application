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
    private String fullName;

    @Length(max = 100)
    private String phoneNumber;

    @NotNull
    @Length(max = 100)
    private String street;

    @Length(max = 100)
    private String additionalInfo;

    @NotNull
    @Length(max = 10)
    private String postalCode;

    @NotNull
    @Length(max = 100)
    private String province;

    @NotNull
    @Length(max = 100)
    private String city;

    @NotNull
    @Length(max = 100)
    private String country;

    @NotNull
    private Boolean isDefault;

}
