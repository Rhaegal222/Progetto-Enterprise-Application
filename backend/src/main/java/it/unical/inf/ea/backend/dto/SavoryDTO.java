package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.enums.SavoryType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class SavoryDTO extends ProductDTO{
    @NotNull
    private SavoryType savoryType;
}
