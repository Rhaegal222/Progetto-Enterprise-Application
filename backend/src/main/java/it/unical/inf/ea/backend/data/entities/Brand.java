package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "descriptionBrand", length = 1000)
    private String descriptionBrand;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Product> products;
}
