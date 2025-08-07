package com.cuackstore.commons.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDTO {
    @NotNull(message = "Stock es requerido")
    private Integer stock;
}
