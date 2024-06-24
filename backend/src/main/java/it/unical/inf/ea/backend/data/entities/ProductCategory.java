package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "productCategories")
public class ProductCategory {
    @Id
    @GeneratedValue
    @Column(length = 36, nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false,unique = true)
    private String categoryName;


    @OneToMany(mappedBy = "productCategory",fetch = FetchType.LAZY)
    private List<Product> products;


    @PrePersist
    @PreUpdate
    public void capitalizeNames() {
        this.categoryName = capitalize(this.categoryName);
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
