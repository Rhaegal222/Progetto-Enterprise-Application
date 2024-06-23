package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @NotEmpty
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Product> products;
}
