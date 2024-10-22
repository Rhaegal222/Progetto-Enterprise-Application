package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserImageDTO {

    @NotNull
    private String id;

    private String description;

    @NotNull
    private String urlPhoto;
}
