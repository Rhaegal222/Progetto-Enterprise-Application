package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.AlcoholicType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class AlcoholicCreateDTO extends ProductCreateDTO{
    @NotNull
    private AlcoholicType alcoholicType;
}
