package com.cuackstore.orders.repository;

import com.cuackstore.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByProductHawa(String productHawa);

    List<OrderItem> findByOrderIdOrderById(Long orderId);

    @Query("SELECT oi.productHawa, oi.productName, SUM(oi.quantity) as totalQuantity, COUNT(oi) as orderCount " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status != 'CANCELADO' " +
            "GROUP BY oi.productHawa, oi.productName " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findBestSellingProducts();

    @Query("SELECT oi.productHawa, oi.productName, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status != 'CANCELADO' " +
            "GROUP BY oi.productHawa, oi.productName " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findProductsSoldInPeriod(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE oi.productHawa = :hawa AND o.status != 'CANCELADO'")
    Long getTotalQuantitySoldByProduct(@Param("hawa") String hawa);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.discountPercentage > 0 ORDER BY oi.discountPercentage DESC")
    List<OrderItem> findItemsWithDiscount();
}
