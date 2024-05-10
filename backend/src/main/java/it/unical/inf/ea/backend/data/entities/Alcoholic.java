package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.AlcoholicType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true) //permette di considerare anche i campi della classe madre
@Data
@NoArgsConstructor
@Table(name = "alcoholic")
@Entity
@PrimaryKeyJoinColumn(name = "product_id") //join con "product_id" come chiave primaria
public class Alcoholic extends Product{

    @Enumerated(EnumType.STRING)
    @Column(name="alcoholic_type", nullable = false)
    private AlcoholicType alcoholicType;
}
