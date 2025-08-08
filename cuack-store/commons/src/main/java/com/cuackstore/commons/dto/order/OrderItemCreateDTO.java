package com.cuackstore.commons.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDTO {

    @NotBlank(message = "HAWA del producto es requerido")
    @Size(max = 50, message = "HAWA no puede exceder 50 caracteres")
    private String productHawa;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Max(value = 100, message = "La cantidad no puede exceder 100 unidades por item")
    private Integer quantity;
}
