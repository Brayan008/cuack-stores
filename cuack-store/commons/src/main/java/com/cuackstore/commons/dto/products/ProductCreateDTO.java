package com.cuackstore.commons.dto.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
    @NotBlank(message = "HAWA es requerido")
    private String hawa;

    @NotBlank(message = "Nombre del producto es requerido")
    private String name;

    private String description;

    @NotNull(message = "Precio de lista es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal listPrice;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal discount;

    @NotNull(message = "Stock es requerido")
    private Integer stock;

    private Boolean available;
}
