package com.cuackstore.commons.dto.order;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    @NotBlank(message = "ID de tienda es requerido")
    private String storeId;

    @NotBlank(message = "Nombre del vendedor es requerido")
    @Size(max = 200, message = "El nombre del vendedor no puede exceder 200 caracteres")
    private String sellerName;

    @NotNull(message = "Datos del cliente son requeridos")
    @Valid
    private CustomerDTO customer;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    private List<OrderItemCreateDTO> items;

    private String comments;

    @Hidden
    private String userIp;
}
