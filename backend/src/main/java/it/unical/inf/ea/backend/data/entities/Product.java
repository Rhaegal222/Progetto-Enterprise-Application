package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.data.entities.embedded.CustomMoney;

import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.ProductSize;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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

    @Column(name = "descriptionBrand", length = 1000)
    private String descriptionBrand;

    @Column(name = "ingredients", length = 1000)
    private String ingredients;

    @Column(name = "nutritionalValues", length = 1000)
    private String nutritionalValues;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="product_price",nullable = false)),
            @AttributeOverride(name="currency",column=@Column(name="product_currency",nullable = false))
    })
    private CustomMoney productCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="delivery_price",nullable = false)),
            @AttributeOverride(name="currency",column=@Column(name="delivery_currency",nullable = false))
    })
    private CustomMoney deliveryCost;

    @Column(name = "brand")
    private String brand;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "last_update_date", nullable = false)
    private LocalDateTime lastUpdateDate;

    //si riferisce alle dimensioni dell'imballo della spedizione
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private ProductSize productSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<ProductImage> productImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

}
