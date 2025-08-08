package com.cuackstore.commons.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String storeId;
    private String sellerName;
    private CustomerDTO customer;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal total;
    private Integer totalQuantity;
    private List<OrderItemResponseDTO> items;
    private String comments;
    private String userIp;

    // Campos calculados
    private boolean canBeCancelled;
    private long minutesSinceCreation;
}
