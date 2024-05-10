package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.SweetType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class SweetCreateDTO extends ProductCreateDTO{
    @NotNull
    private SweetType sweetType;
}
