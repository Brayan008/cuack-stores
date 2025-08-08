package com.cuackstore.commons.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {

    @NotNull(message = "El nuevo estatus es requerido")
    @Pattern(regexp = "PENDIENTE|ENTREGADO|CANCELADO",
            message = "El estatus debe ser: PENDIENTE, ENTREGADO o CANCELADO")
    private String status;

    private String reason; // Razón del cambio de estatus
}
