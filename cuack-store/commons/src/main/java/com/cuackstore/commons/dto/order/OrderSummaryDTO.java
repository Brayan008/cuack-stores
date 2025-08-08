package com.cuackstore.commons.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String storeId;
    private String sellerName;
    private String customerName;
    private String customerEmail;
    private String status;
    private BigDecimal total;
    private Integer totalItems;
    private Integer totalQuantity;
    private boolean canBeCancelled;
}
