package com.cuackstore.commons.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {

    private String productHawa;
    private String productName;
    private Long totalQuantitySold;
    private Long orderCount;
    private BigDecimal totalRevenue;
}