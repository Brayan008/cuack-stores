package com.cuackstore.orders.service.impl;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.order.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.order.InventoryProductDTO;
import com.cuackstore.commons.dto.order.StockOperationDTO;
import com.cuackstore.orders.client.InventoryClient;
import com.cuackstore.orders.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryClient inventoryClient;

    @Override
    public InventoryProductDTO getProduct(String hawa) {

        log.info("Obteniendo producto del inventario: {}", hawa);

        ApiResponseDTO<InventoryProductDTO> response = inventoryClient.getProductByHawa(hawa);
        return response.getData();
    }

    @Override
    public AvailabilityResponseDTO checkAvailability(String hawa) {

        log.info("Verificando disponibilidad del producto: {}", hawa);

        ApiResponseDTO<AvailabilityResponseDTO> response = inventoryClient.checkAvailability(hawa);
        return response.getData();

    }

    @Override
    public boolean decrementStock(String hawa, Integer quantity) {

        log.info("Decrementando stock del producto: {} en {} unidades", hawa, quantity);

        StockOperationDTO operation = StockOperationDTO.builder()
                .quantity(quantity)
                .build();

        ApiResponseDTO<InventoryProductDTO> response =
                inventoryClient.decrementStock(hawa, operation);

        if (response.getData() != null) {
            log.info("Stock decrementado exitosamente para {}", hawa);
            return true;
        } else {
            log.warn("No se pudo decrementar stock para {}: {}",
                    hawa, response.getMessage() != null ? response.getMessage() : "Error desconocido");
            return false;
        }

    }

    @Override
    public boolean incrementStock(String hawa, Integer quantity) {

        log.info("Incrementando stock del producto: {} en {} unidades", hawa, quantity);

        StockOperationDTO operation = StockOperationDTO.builder()
                .quantity(quantity)
                .build();

        ApiResponseDTO<InventoryProductDTO> response =
                inventoryClient.incrementStock(hawa, operation);

        if (response.getData() != null) {
            log.info("Stock incrementado exitosamente para {}", hawa);
            return true;
        } else {
            log.warn("No se pudo incrementar stock para {}: {}",
                    hawa, response.getMessage() != null ? response.getMessage() : "Error desconocido");
            return false;
        }

    }
}
