package it.unical.inf.ea.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDTO {

    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    private String name;
}
