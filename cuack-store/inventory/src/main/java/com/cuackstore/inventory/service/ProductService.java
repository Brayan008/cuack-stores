package com.cuackstore.inventory.service;

import com.cuackstore.commons.dto.products.ProductCreateDTO;
import com.cuackstore.commons.dto.products.ProductResponseDTO;
import com.cuackstore.commons.dto.stock.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.stock.StockOperationDTO;
import com.cuackstore.commons.dto.stock.StockUpdateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<ProductResponseDTO> getAllProducts();
    Mono<ProductResponseDTO> getProductByHawa(String hawa);
    Mono<AvailabilityResponseDTO> checkAvailability(String hawa);
    Flux<ProductResponseDTO> getAvailableProducts();
    Mono<ProductResponseDTO> createProduct(ProductCreateDTO createDTO);
    Mono<ProductResponseDTO> updateStock(String hawa, StockUpdateDTO stockUpdateDTO);
    Mono<ProductResponseDTO> incrementStock(String hawa, StockOperationDTO operationDTO);
    Mono<ProductResponseDTO> decrementStock(String hawa, StockOperationDTO operationDTO);
    Flux<ProductResponseDTO> getProductsWithLowStock(Integer threshold);
    Mono<ProductResponseDTO> updateAvailability(String hawa, Boolean available);

}
