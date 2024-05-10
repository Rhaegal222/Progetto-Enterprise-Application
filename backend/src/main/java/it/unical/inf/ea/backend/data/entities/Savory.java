package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.SavoryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true) //permette di considerare anche i campi della classe madre
@Data
@NoArgsConstructor
@Table(name = "savory")
@Entity
@PrimaryKeyJoinColumn(name = "product_id") //join con "product_id" come chiave primaria
public class Savory extends Product{

    @Enumerated(EnumType.STRING)
    @Column(name="savory_type", nullable = false)
    private SavoryType savoryType;
}
