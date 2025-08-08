package com.cuackstore.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_hawa", nullable = false, length = 50)
    @NotBlank(message = "HAWA del producto es requerido")
    private String productHawa;

    @Column(name = "product_name", nullable = false, length = 200)
    @NotBlank(message = "Nombre del producto es requerido")
    private String productName;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Cantidad es requerida")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Precio unitario es requerido")
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    // Métodos de cálculo
    public BigDecimal calculateDiscountAmount() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal totalBeforeDiscount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return totalBeforeDiscount.subtract(calculateDiscountAmount());
    }

    public BigDecimal getFinalUnitPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return unitPrice;
        }
        BigDecimal discountPerUnit = unitPrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        return unitPrice.subtract(discountPerUnit);
    }

    // PrePersist y PreUpdate para calcular automáticamente
    @PrePersist
    @PreUpdate
    public void calculateAmounts() {
        this.discountAmount = calculateDiscountAmount();
        this.subtotal = calculateSubtotal();
    }
}
