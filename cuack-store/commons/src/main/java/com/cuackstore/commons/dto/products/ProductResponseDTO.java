package com.cuackstore.commons.dto.products;

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
public class ProductResponseDTO {
    private Long id;
    private String hawa;
    private String name;
    private String description;
    private BigDecimal listPrice;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private Integer stock;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
