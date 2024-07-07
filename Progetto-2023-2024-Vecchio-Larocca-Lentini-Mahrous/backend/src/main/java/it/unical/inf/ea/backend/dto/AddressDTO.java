package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {

    private UUID id;

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
