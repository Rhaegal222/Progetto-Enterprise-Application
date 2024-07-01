package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.Availability;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "last_update_date", nullable = false)
    private LocalDateTime lastUpdateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Column(name = "productWeight")
    private String productWeight;

    @Column(name = "quantity",nullable = false)
    @Min(0)
    @Max(1000)
    private int quantity;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductImage photoProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "on_sale", nullable = false)
    private boolean onSale;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "discounted_price", nullable = true)
    private BigDecimal discountedPrice;


}
