package com.cuackstore.commons.dto.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    private String name;
    private String description;
    private BigDecimal listPrice;
    private BigDecimal discount;
    private Integer stock;
    private Boolean available;
}
