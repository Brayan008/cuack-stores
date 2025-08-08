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
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    List<Order> findAllByOrderByCreatedAtDesc();

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findBySellerNameContainingIgnoreCaseOrderByCreatedAtDesc(String sellerName);

    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :customerName, '%')) ORDER BY o.createdAt DESC")
    List<Order> findByCustomerNameContainingIgnoreCase(@Param("customerName") String customerName);

    List<Order> findByStoreIdOrderByCreatedAtDesc(String storeId);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    List<Order> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDIENTE' AND o.createdAt > :tenMinutesAgo ORDER BY o.createdAt DESC")
    List<Order> findCancellablePendingOrders(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDIENTE' AND o.createdAt <= :tenMinutesAgo ORDER BY o.createdAt DESC")
    List<Order> findNonCancellablePendingOrders(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);

    long countByStatus(OrderStatus status);

    long countBySellerName(String sellerName);

    long countByStoreId(String storeId);

    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.email) = LOWER(:email) ORDER BY o.createdAt DESC")
    List<Order> findByCustomerEmail(@Param("email") String email);

    @Query("SELECT o FROM Order o WHERE o.customer.phone = :phone ORDER BY o.createdAt DESC")
    List<Order> findByCustomerPhone(@Param("phone") String phone);

    @Query("SELECT o FROM Order o WHERE o.customer.document = :document ORDER BY o.createdAt DESC")
    List<Order> findByCustomerDocument(@Param("document") String document);

    @Query("SELECT YEAR(o.createdAt) as year, MONTH(o.createdAt) as month, COUNT(o) as total " +
            "FROM Order o " +
            "WHERE o.createdAt >= :startDate " +
            "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getOrderStatsByMonth(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT o.status, COUNT(o), SUM(o.total) FROM Order o GROUP BY o.status")
    List<Object[]> getTotalsByStatus();

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.productHawa = :hawa ORDER BY o.createdAt DESC")
    List<Order> findOrdersContainingProduct(@Param("hawa") String hawa);

    @Query("SELECT o FROM Order o WHERE LOWER(o.customer.email) = LOWER(:email) ORDER BY o.createdAt DESC")
    Optional<Order> findFirstByCustomerEmailOrderByCreatedAtDesc(@Param("email") String email);

    /**
     * Verificar si existe un pedido reciente del mismo cliente
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE LOWER(o.customer.email) = LOWER(:email) AND o.createdAt > :since")
    boolean existsRecentOrderByCustomer(@Param("email") String email, @Param("since") LocalDateTime since);
}
