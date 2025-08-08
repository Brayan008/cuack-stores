package com.cuackstore.orders.client.impl;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.order.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.order.InventoryProductDTO;
import com.cuackstore.commons.dto.order.StockOperationDTO;
import com.cuackstore.orders.client.InventoryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class InventoryClientFallback implements InventoryClient {

    @Override
    public ApiResponseDTO<InventoryProductDTO> getProductByHawa(String hawa) {
        log.warn("Fallback activado para getProductByHawa - HAWA: {}", hawa);

        InventoryProductDTO product = InventoryProductDTO.builder()
                .hawa(hawa)
                .name("Producto no disponible")
                .description("Servicio de inventario no disponible")
                .listPrice(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .finalPrice(BigDecimal.ZERO)
                .stock(0)
                .available(false)
                .build();

        return ApiResponseDTO.handleBuild(product, "Servicio de inventario no disponible");
    }

    @Override
    public ApiResponseDTO<AvailabilityResponseDTO> checkAvailability(String hawa) {
        return null;
    }

    @Override
    public ApiResponseDTO<InventoryProductDTO> decrementStock(String hawa, StockOperationDTO stockOperation) {
        return null;
    }

    @Override
    public ApiResponseDTO<InventoryProductDTO> incrementStock(String hawa, StockOperationDTO stockOperation) {
        return null;
    }

}
