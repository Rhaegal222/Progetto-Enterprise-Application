package it.unical.inf.ea.backend.dto.creation;

import it.unical.inf.ea.backend.dto.enums.SavoryType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class SavoryCreateDTO extends ProductCreateDTO{
    @NotNull
    private SavoryType savoryType;
}
