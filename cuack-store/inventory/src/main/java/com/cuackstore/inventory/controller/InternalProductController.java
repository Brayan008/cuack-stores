package com.cuackstore.inventory.controller;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.products.ProductResponseDTO;
import com.cuackstore.commons.dto.stock.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.stock.StockOperationDTO;
import com.cuackstore.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InternalProductController {

    private final ProductService productService;

    @GetMapping("/product/{hawa}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> getProductByHawa(
            @PathVariable @NotBlank String hawa) {
        log.info("GET /api/inventory/product/{} - Obteniendo producto", hawa);

        return productService.getProductByHawa(hawa)
                .map(product ->
                        ApiResponseDTO.handleBuild(product, "Producto encontrado exitosamente")
                );
    }

    @PutMapping("/product/{hawa}/stock/increment")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> incrementStock(
            @PathVariable @NotBlank String hawa,
            @Valid @RequestBody StockOperationDTO operationDTO) {
        log.info("PUT /api/inventory/product/{}/stock/increment - Incrementando en {}",
                hawa, operationDTO.getQuantity());

        return productService.incrementStock(hawa, operationDTO)
                .map(product ->
                        ApiResponseDTO.handleBuild(product, "Stock incrementado exitosamente"));
    }

    @PutMapping("/product/{hawa}/stock/decrement")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> decrementStock(
            @PathVariable @NotBlank String hawa,
            @Valid @RequestBody StockOperationDTO operationDTO) {
        log.info("PUT /api/inventory/product/{}/stock/decrement - Decrementando en {}",
                hawa, operationDTO.getQuantity());

        return productService.decrementStock(hawa, operationDTO)
                .map(product -> ApiResponseDTO.handleBuild(product, "Stock decrementado exitosamente"));
    }

}
