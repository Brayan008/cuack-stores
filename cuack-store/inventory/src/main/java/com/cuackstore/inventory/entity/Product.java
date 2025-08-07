package com.cuackstore.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "HAWA es requerido")
    @Column("hawa")
    private String hawa;

    @NotBlank(message = "Nombre del producto es requerido")
    @Column("name")
    private String name;

    @NotBlank(message = "Descripción es requerida")
    @Column("description")
    private String description;

    @NotNull(message = "Precio de lista es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column("list_price")
    private BigDecimal listPrice;

    @Builder.Default
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column("discount")
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull(message = "Stock es requerido")
    @Column("stock")
    private Integer stock;

    @Builder.Default
    @Column("available")
    private Boolean available = true;

    @Builder.Default
    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private String createdBy;

    public boolean hasStock() {
        return stock != null && stock > 0;
    }

    public boolean isAvailable() {
        return available != null && available && hasStock();
    }

    public BigDecimal getFinalPrice() {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return listPrice;
        }
        BigDecimal discountAmount = listPrice.multiply(discount).divide(BigDecimal.valueOf(100));
        return listPrice.subtract(discountAmount);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
