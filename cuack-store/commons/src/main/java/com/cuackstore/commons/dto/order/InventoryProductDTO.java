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
public class InventoryProductDTO {

    private String hawa;
    private String name;
    private String description;
    private BigDecimal listPrice;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private Integer stock;
    private Boolean available;
}
