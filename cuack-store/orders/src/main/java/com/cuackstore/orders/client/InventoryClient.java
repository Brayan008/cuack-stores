package com.cuackstore.orders.client;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.order.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.order.InventoryProductDTO;
import com.cuackstore.commons.dto.order.StockOperationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "msvc-inventory"
        //fallback = InventoryClientFallback.class
)
public interface InventoryClient {

    @GetMapping("/v1/inventory/product/{hawa}")
    ApiResponseDTO<InventoryProductDTO> getProductByHawa(@PathVariable String hawa);

    @GetMapping("/api/v1/inventory/product/{hawa}/availability")
    ApiResponseDTO<AvailabilityResponseDTO> checkAvailability(@PathVariable String hawa);

    @PutMapping("/v1/inventory/product/{hawa}/stock/decrement")
    ApiResponseDTO<InventoryProductDTO> decrementStock(
            @PathVariable String hawa,
            @RequestBody StockOperationDTO stockOperation
    );

    @PutMapping("/v1/inventory/product/{hawa}/stock/increment")
    ApiResponseDTO<InventoryProductDTO> incrementStock(
            @PathVariable String hawa,
            @RequestBody StockOperationDTO stockOperation
    );
}
