package com.cuackstore.commons.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOperationDTO {
    @NotNull(message = "Cantidad es requerida")
    @DecimalMin(value = "1", message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
}
