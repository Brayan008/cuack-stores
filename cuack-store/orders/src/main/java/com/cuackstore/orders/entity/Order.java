package com.cuackstore.orders.entity;

import com.cuackstore.commons.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_ip", length = 45)
    private String userIp;

    @Column(name = "store_id", nullable = false)
    @NotNull(message = "ID de tienda es requerido")
    private String storeId;

    @Column(name = "seller_name", nullable = false, length = 200)
    @NotBlank(message = "Nombre del vendedor es requerido")
    private String sellerName;

    // Datos del cliente (embedded)
    @Embedded
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDIENTE;

    @Column(name = "subtotal", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "total_discount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "total", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "comments", length = 1000)
    private String comments;

    // Métodos de utilidad
    public boolean canBeCancelled() {
        if (status != OrderStatus.PENDIENTE) {
            return false;
        }
        // Verificar que no hayan pasado más de 10 minutos
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        return createdAt.isAfter(tenMinutesAgo);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalDiscount = items.stream()
                .map(OrderItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = subtotal.subtract(totalDiscount);
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotals();
    }

    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
}
