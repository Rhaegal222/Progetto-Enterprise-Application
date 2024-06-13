package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;


    @Column(name = "ingredients", length = 1000)
    private String ingredients;

    @Column(name = "nutritionalValues", length = 1000)
    private String nutritionalValues;

    @Column(name = "productPrice", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "deliveryPrice", nullable = false)
    private BigDecimal deliveryPrice;

    @Column(name = "brand")
    private String brand;

    //si riferisce alle dimensioni dell'imballo della spedizione
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private ProductSize productSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory productCategory;


    @Column(name = "like_number", nullable = false)
    private Integer likesNumber ;


    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Order> order = new ArrayList<>();


    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<ProductImage> productImages = new ArrayList<>();


    @OneToMany(mappedBy = "reportedProduct",fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();


    @PreRemove
    private void preRemove(){
        for(Report report:this.reports){
            report.setReportedProduct(null);
        }
    }

}
