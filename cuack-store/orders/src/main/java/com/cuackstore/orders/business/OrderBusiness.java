package com.cuackstore.orders.business;

import com.cuackstore.commons.dto.order.*;
import com.cuackstore.commons.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderBusiness {
    OrderResponseDTO createOrder(OrderCreateDTO createDTO);
    Page<OrderSummaryDTO> getAllOrders(Pageable pageable);
    List<OrderSummaryDTO> getOrdersByStatus(OrderStatus status);
    OrderResponseDTO getOrderById(Long id);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusDTO);
    List<OrderSummaryDTO> searchOrdersByCustomer(String customerName);
    List<OrderSummaryDTO> searchOrdersBySeller(String sellerName);
    OrderStatsDTO getOrderStatistics();
}
