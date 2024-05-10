package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.SweetType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true) //permette di considerare anche i campi della classe madre
@Data
@NoArgsConstructor
@Table(name = "sweet")
@Entity
@PrimaryKeyJoinColumn(name = "product_id") //join con "product_id" come chiave primaria
public class Sweet extends Product{

    @Enumerated(EnumType.STRING)
    @Column(name="sweet_type", nullable = false)
    private SweetType sweetType;
}
