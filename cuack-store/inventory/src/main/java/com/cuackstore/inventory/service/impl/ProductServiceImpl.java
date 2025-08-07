package com.cuackstore.inventory.service.impl;

import com.cuackstore.commons.dto.products.ProductCreateDTO;
import com.cuackstore.commons.dto.products.ProductResponseDTO;
import com.cuackstore.commons.dto.stock.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.stock.StockOperationDTO;
import com.cuackstore.commons.dto.stock.StockUpdateDTO;
import com.cuackstore.commons.exceptions.ServicesException;
import com.cuackstore.inventory.entity.Product;
import com.cuackstore.inventory.repository.ProductRepository;
import com.cuackstore.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Flux<ProductResponseDTO> getAllProducts() {
        log.info("Obteniendo todos los productos");
        return productRepository.findAll()
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.debug("Producto obtenido: {}", product.getHawa()));
    }

    @Override
    public Mono<ProductResponseDTO> getProductByHawa(String hawa) {
        log.info("Buscando producto con HAWA: {}", hawa);
        return productRepository.findByHawa(hawa)
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Producto encontrado: {}", product.getHawa()))
                .switchIfEmpty(Mono.error(new ServicesException("Producto no encontrado con HAWA: " + hawa, HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<AvailabilityResponseDTO> checkAvailability(String hawa) {
        log.info("Verificando disponibilidad para HAWA: {}", hawa);
        return productRepository.findByHawa(hawa)
                .map(product -> {
                    if (!product.getAvailable()) {
                        return AvailabilityResponseDTO.unavailable(hawa, "Producto deshabilitado");
                    }
                    if (!product.hasStock()) {
                        return AvailabilityResponseDTO.unavailable(hawa, "Sin existencias");
                    }
                    return AvailabilityResponseDTO.available(hawa, product.getStock());
                })
                .switchIfEmpty(Mono.just(AvailabilityResponseDTO.unavailable(hawa, "Producto no encontrado")))
                .doOnNext(availability -> log.info("Disponibilidad para {}: {}", hawa, availability.getAvailable()));
    }

    @Override
    public Flux<ProductResponseDTO> getAvailableProducts() {
        log.info("Obteniendo productos disponibles");
        return productRepository.findAvailableProducts()
                .map(this::mapToResponseDTO);
    }

    @Override
    public Mono<ProductResponseDTO> createProduct(ProductCreateDTO createDTO) {
        log.info("Creando producto con HAWA: {}", createDTO.getHawa());

        return productRepository.countByHawa(createDTO.getHawa())
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new ServicesException("Ya existe un producto con HAWA: " + createDTO.getHawa(), HttpStatus.BAD_REQUEST));
                    }

                    Product product = Product.builder()
                            .hawa(createDTO.getHawa())
                            .name(createDTO.getName())
                            .description(createDTO.getDescription())
                            .listPrice(createDTO.getListPrice())
                            .discount(createDTO.getDiscount() != null ? createDTO.getDiscount() : java.math.BigDecimal.ZERO)
                            .stock(createDTO.getStock())
                            .available(createDTO.getAvailable() != null ? createDTO.getAvailable() : true)
                            .createdAt(LocalDateTime.now())
                            .createdBy("system") // En un caso real, obtener del contexto de seguridad
                            .build();

                    return productRepository.save(product);
                })
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Producto creado exitosamente: {}", product.getHawa()));
    }

    @Override
    public Mono<ProductResponseDTO> updateStock(String hawa, StockUpdateDTO stockUpdateDTO) {
        log.info("Actualizando stock para HAWA: {} a {}", hawa, stockUpdateDTO.getStock());

        return productRepository.updateStockByHawa(hawa, stockUpdateDTO.getStock())
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == 0) {
                        return Mono.error(new ServicesException("Producto no encontrado con HAWA: " + hawa, HttpStatus.NOT_FOUND));
                    }
                    return productRepository.findByHawa(hawa);
                })
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Stock actualizado para {}: {}", hawa, product.getStock()));
    }

    @Override
    public Mono<ProductResponseDTO> incrementStock(String hawa, StockOperationDTO operationDTO) {
        log.info("Incrementando stock para HAWA: {} en {}", hawa, operationDTO.getQuantity());

        return productRepository.incrementStock(hawa, operationDTO.getQuantity())
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == 0) {
                        return Mono.error(new ServicesException("Producto no encontrado con HAWA: " + hawa, HttpStatus.NOT_FOUND));
                    }
                    return productRepository.findByHawa(hawa);
                })
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Stock incrementado para {}: {}", hawa, product.getStock()));
    }

    @Override
    public Mono<ProductResponseDTO> decrementStock(String hawa, StockOperationDTO operationDTO) {
        log.info("Decrementando stock para HAWA: {} en {}", hawa, operationDTO.getQuantity());

        return productRepository.decrementStock(hawa, operationDTO.getQuantity())
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == 0) {
                        return productRepository.findByHawa(hawa)
                                .flatMap(product -> {
                                    if (product.getStock() < operationDTO.getQuantity()) {
                                        return Mono.error(new ServicesException("Stock insuficiente. Stock actual: " + product.getStock(), HttpStatus.CONFLICT));
                                    }
                                    return Mono.error(new ServicesException("Producto no encontrado con HAWA: " + hawa, HttpStatus.NOT_FOUND));
                                });
                    }
                    return productRepository.findByHawa(hawa);
                })
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Stock decrementado para {}: {}", hawa, product.getStock()));
    }

    @Override
    public Flux<ProductResponseDTO> getProductsWithLowStock(Integer threshold) {
        log.info("Obteniendo productos con stock bajo (threshold: {})", threshold);
        return productRepository.findProductsWithLowStock(threshold)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Mono<ProductResponseDTO> updateAvailability(String hawa, Boolean available) {
        log.info("Actualizando disponibilidad para HAWA: {} a {}", hawa, available);

        return productRepository.updateAvailabilityByHawa(hawa, available)
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == 0) {
                        return Mono.error(new ServicesException("Producto no encontrado con HAWA: " + hawa, HttpStatus.NOT_FOUND));
                    }
                    return productRepository.findByHawa(hawa);
                })
                .map(this::mapToResponseDTO)
                .doOnNext(product -> log.info("Disponibilidad actualizada para {}: {}", hawa, product.getAvailable()));
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .hawa(product.getHawa())
                .name(product.getName())
                .description(product.getDescription())
                .listPrice(product.getListPrice())
                .discount(product.getDiscount())
                .finalPrice(product.getFinalPrice())
                .stock(product.getStock())
                .available(product.getAvailable())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
