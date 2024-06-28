package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {

    private String id;

    @NotNull
    @Length(max = 100)
    private String header;

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
