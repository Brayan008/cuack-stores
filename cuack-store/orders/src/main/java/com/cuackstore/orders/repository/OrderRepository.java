package com.cuackstore.orders.repository;

import com.cuackstore.commons.enums.OrderStatus;
import com.cuackstore.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findBySellerNameContainingIgnoreCaseOrderByCreatedAtDesc(String sellerName);

    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :customerName, '%')) ORDER BY o.createdAt DESC")
    List<Order> findByCustomerName(@Param("customerName") String customerName);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    long countByStatus(OrderStatus status);

}
