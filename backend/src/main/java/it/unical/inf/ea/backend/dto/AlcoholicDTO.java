package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.AlcoholicType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class AlcoholicDTO extends ProductDTO{
    @NotNull
    private AlcoholicType alcoholicType;
}

