package com.cuackstore.inventory.controller;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.products.ProductCreateDTO;
import com.cuackstore.commons.dto.products.ProductResponseDTO;
import com.cuackstore.commons.dto.stock.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.stock.StockOperationDTO;
import com.cuackstore.commons.dto.stock.StockUpdateDTO;
import com.cuackstore.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Flux<ProductResponseDTO> getAllProducts() {
        log.info("GET /api/inventory/products - Obteniendo todos los productos");

        return productService.getAllProducts();
    }

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

    @GetMapping("/product/{hawa}/availability")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<AvailabilityResponseDTO>> checkAvailability(
            @PathVariable @NotBlank String hawa) {
        log.info("GET /api/inventory/product/{}/availability - Verificando disponibilidad", hawa);

        return productService.checkAvailability(hawa)
                .map(availability -> {
                    if (availability.getAvailable()) {
                        return ApiResponseDTO.handleBuild(availability, "Producto disponible");
                    } else {
                        return ApiResponseDTO.handleBuild(availability, availability.getMessage());
                    }
                });
    }

    @GetMapping("/products/available")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Flux<ProductResponseDTO> getAvailableProducts() {
        log.info("GET /api/inventory/products/available - Obteniendo productos disponibles");

        return productService.getAvailableProducts();

    }

    @PostMapping("/product")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO) {
        log.info("POST /api/inventory/product - Creando producto con HAWA: {}", createDTO.getHawa());

        return productService.createProduct(createDTO)
                .map(product -> ApiResponseDTO.handleBuild(product, "Producto creado exitosamente"));
    }

    @PutMapping("/product/{hawa}/stock")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> updateStock(
            @PathVariable @NotBlank String hawa,
            @Valid @RequestBody StockUpdateDTO stockUpdateDTO) {
        log.info("PUT /api/inventory/product/{}/stock - Actualizando stock a {}",
                hawa, stockUpdateDTO.getStock());

        return productService.updateStock(hawa, stockUpdateDTO)
                .map(product ->
                        ApiResponseDTO.handleBuild(product, "Stock actualizado exitosamente")
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

    @GetMapping("/products/low-stock")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Flux<ProductResponseDTO> getProductsWithLowStock(
            @RequestParam(defaultValue = "5") @Positive Integer threshold) {
        log.info("GET /api/inventory/products/low-stock - Threshold: {}", threshold);

        return productService.getProductsWithLowStock(threshold);
    }

    @PutMapping("/product/{hawa}/availability")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ApiResponseDTO<ProductResponseDTO>> updateAvailability(
            @PathVariable @NotBlank String hawa,
            @RequestParam Boolean available) {
        log.info("PUT /api/inventory/product/{}/availability - Disponibilidad: {}", hawa, available);

        return productService.updateAvailability(hawa, available)
                .map(product ->
                        ApiResponseDTO.handleBuild(product, "Disponibilidad actualizada exitosamente")
                );
    }

    @GetMapping("/health")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public Mono<ResponseEntity<ApiResponseDTO<String>>> healthCheck() {
        return Mono.just(ResponseEntity.ok(
                ApiResponseDTO.handleBuild("OK", "Inventory Service is running")
        ));
    }
}
