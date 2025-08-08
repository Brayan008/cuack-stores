package com.cuackstore.orders.controller;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.dto.order.*;
import com.cuackstore.commons.enums.OrderStatus;
import com.cuackstore.orders.business.OrderBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {
    private final OrderBusiness orderService;


    @PostMapping
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderCreateDTO createDTO,
            HttpServletRequest request) {

        log.info("POST /api/orders - Creando pedido para cliente: {}", createDTO.getCustomer().getName());

        // Capturar IP del usuario
        String userIp = getClientIpAddress(request);
        createDTO.setUserIp(userIp);

        OrderResponseDTO order = orderService.createOrder(createDTO);

        log.info("Pedido creado exitosamente con ID: {}", order.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.handleBuild(order, "Pedido creado exitosamente"));

    }

    @GetMapping
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer size) {

        log.info("GET /api/orders - Página: {}, Tamaño: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderSummaryDTO> orders = orderService.getAllOrders(pageable);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(orders, "Pedidos obtenidos exitosamente")
        );

    }


    @GetMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> getOrderById(@PathVariable @Positive Long id) {
        log.info("GET /api/orders/{} - Obteniendo detalle del pedido", id);

        OrderResponseDTO order = orderService.getOrderById(id);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(order, "Detalle del pedido obtenido exitosamente")
        );
    }

    @PutMapping("/{id}/status")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable @Positive Long id,
            @Valid @RequestBody OrderStatusUpdateDTO statusDTO) {

        log.info("PUT /api/orders/{}/status - Cambiando estatus a: {}", id, statusDTO.getStatus());

        OrderResponseDTO order = orderService.updateOrderStatus(id, statusDTO);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(order, "Estatus del pedido actualizado exitosamente")
        );
    }

    @GetMapping("/status/{status}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable OrderStatus status) {

        log.info("GET /api/orders/status/{} - Obteniendo pedidos por estatus", status);
        List<OrderSummaryDTO> orders = orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(orders, "Pedidos filtrados por estatus obtenidos exitosamente")
        );

    }

    @GetMapping("/search/customer")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<ApiResponseDTO<List<OrderSummaryDTO>>> searchOrdersByCustomer(
            @RequestParam @NotBlank String name) {

        log.info("GET /api/orders/search/customer?name={}", name);

        List<OrderSummaryDTO> orders = orderService.searchOrdersByCustomer(name);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(orders, "Pedidos del cliente encontrados exitosamente")
        );

    }

    @GetMapping("/search/seller")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<ApiResponseDTO<List<OrderSummaryDTO>>> searchOrdersBySeller(
            @RequestParam @NotBlank String name) {

        log.info("GET /api/orders/search/seller?name={}", name);

        List<OrderSummaryDTO> orders = orderService.searchOrdersBySeller(name);

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(orders, "Pedidos del vendedor encontrados exitosamente")
        );

    }

    @GetMapping("/stats")
    @Operation(
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> getOrderStatistics() {
        log.info("GET /api/orders/stats - Obteniendo estadísticas");

        OrderStatsDTO stats = orderService.getOrderStatistics();

        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild(stats, "Estadísticas obtenidas exitosamente")
        );

    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponseDTO.handleBuild("OK", "Orders Service is running")
        );
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
