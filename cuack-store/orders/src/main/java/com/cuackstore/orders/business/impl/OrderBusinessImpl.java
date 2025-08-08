package com.cuackstore.orders.business.impl;

import com.cuackstore.commons.dto.order.*;
import com.cuackstore.commons.enums.OrderStatus;
import com.cuackstore.orders.business.OrderBusiness;
import com.cuackstore.orders.entity.Customer;
import com.cuackstore.orders.entity.Order;
import com.cuackstore.orders.entity.OrderItem;
import com.cuackstore.orders.repository.OrderRepository;
import com.cuackstore.orders.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderBusinessImpl implements OrderBusiness {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Override
    public OrderResponseDTO createOrder(OrderCreateDTO createDTO) {
        log.info("Iniciando creación de pedido para cliente: {}", createDTO.getCustomer().getName());

        try {
            for (OrderItemCreateDTO itemDTO : createDTO.getItems()) {
                AvailabilityResponseDTO availability = inventoryService.checkAvailability(itemDTO.getProductHawa());

                if (!availability.getAvailable()) {
                    throw new RuntimeException("Producto no disponible: " + itemDTO.getProductHawa() +
                            " - " + availability.getMessage());
                }

                if (availability.getStock() < itemDTO.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente para producto: " + itemDTO.getProductHawa() +
                            ". Stock disponible: " + availability.getStock() +
                            ", Solicitado: " + itemDTO.getQuantity());
                }
            }

            Order order = Order.builder()
                    .storeId(createDTO.getStoreId())
                    .sellerName(createDTO.getSellerName())
                    .customer(mapToCustomerEntity(createDTO.getCustomer()))
                    .status(OrderStatus.PENDIENTE)
                    .comments(createDTO.getComments())
                    .userIp(createDTO.getUserIp())
                    .build();

            for (OrderItemCreateDTO itemDTO : createDTO.getItems()) {
                InventoryProductDTO product = inventoryService.getProduct(itemDTO.getProductHawa());

                OrderItem orderItem = OrderItem.builder()
                        .productHawa(product.getHawa())
                        .productName(product.getName())
                        .quantity(itemDTO.getQuantity())
                        .unitPrice(product.getListPrice())
                        .discountPercentage(product.getDiscount())
                        .build();

                order.addItem(orderItem);
            }

            order = orderRepository.save(order);
            log.info("Pedido creado con ID: {}", order.getId());

            for (OrderItem item : order.getItems()) {
                boolean stockDecremented = inventoryService.decrementStock(
                        item.getProductHawa(),
                        item.getQuantity()
                );

                if (!stockDecremented) {
                    log.warn("No se pudo decrementar stock para producto: {}", item.getProductHawa());
                }
            }

            return mapToOrderResponseDTO(order);

        } catch (Exception e) {
            log.error("Error creando pedido: {}", e.getMessage());
            throw new RuntimeException("Error al crear el pedido: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderSummaryDTO> getAllOrders(Pageable pageable) {
        log.info("Obteniendo pedidos con paginación: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToOrderSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> getOrdersByStatus(OrderStatus status) {
        log.info("Obteniendo pedidos por estatus: {}", status);

        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::mapToOrderSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        log.info("Obteniendo detalle del pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        return mapToOrderResponseDTO(order);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusDTO) {
        log.info("Actualizando estatus del pedido ID: {} a {}", id, statusDTO.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        OrderStatus newStatus = OrderStatus.valueOf(statusDTO.getStatus());
        OrderStatus previousStatus = order.getStatus();

        validateStatusChange(order, newStatus);

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        handleStockChangesForStatusUpdate(order, previousStatus, newStatus);

        log.info("Estatus del pedido {} cambiado de {} a {}", id, previousStatus, newStatus);
        return mapToOrderResponseDTO(order);
    }


    @Transactional(readOnly = true)
    @Override
    public List<OrderSummaryDTO> searchOrdersByCustomer(String customerName) {
        log.info("Buscando pedidos por cliente: {}", customerName);

        return orderRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::mapToOrderSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> searchOrdersBySeller(String sellerName) {
        log.info("Buscando pedidos por vendedor: {}", sellerName);

        return orderRepository.findBySellerNameContainingIgnoreCaseOrderByCreatedAtDesc(sellerName)
                .stream()
                .map(this::mapToOrderSummaryDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Override
    public OrderStatsDTO getOrderStatistics() {
        log.info("Obteniendo estadísticas de pedidos");

        List<Object[]> statsByStatus = orderRepository.getTotalsByStatus();

        return OrderStatsDTO.builder()
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDIENTE))
                .deliveredOrders(orderRepository.countByStatus(OrderStatus.ENTREGADO))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELADO))
                .build();
    }

    private void validateStatusChange(Order order, OrderStatus newStatus) {
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            throw new RuntimeException("El pedido ya tiene el estatus: " + newStatus);
        }

        if (currentStatus != OrderStatus.PENDIENTE) {
            throw new RuntimeException("Solo se puede cambiar el estatus de pedidos pendientes");
        }

        if (newStatus == OrderStatus.CANCELADO && !order.canBeCancelled()) {
            throw new RuntimeException("No se puede cancelar el pedido. Han pasado más de 10 minutos desde su creación.");
        }
    }

    private void handleStockChangesForStatusUpdate(Order order, OrderStatus previousStatus, OrderStatus newStatus) {

        if (newStatus == OrderStatus.CANCELADO && previousStatus == OrderStatus.PENDIENTE) {
            log.info("Devolviendo stock al inventario por cancelación del pedido {}", order.getId());

            for (OrderItem item : order.getItems()) {
                boolean stockIncremented = inventoryService.incrementStock(
                        item.getProductHawa(),
                        item.getQuantity()
                );

                if (stockIncremented) {
                    log.info("Stock devuelto para producto {}: {} unidades",
                            item.getProductHawa(), item.getQuantity());
                } else {
                    log.warn("No se pudo devolver stock para producto: {}", item.getProductHawa());
                }
            }
        }

        // Si el pedido se entrega, el stock ya fue decrementado al crear el pedido
        // No se requiere acción adicional
    }

    private Customer mapToCustomerEntity(CustomerDTO dto) {
        return Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .document(dto.getDocument())
                .documentType(dto.getDocumentType())
                .build();
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        long minutesSinceCreation = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();

        return OrderResponseDTO.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .storeId(order.getStoreId())
                .sellerName(order.getSellerName())
                .customer(mapToCustomerDTO(order.getCustomer()))
                .status(order.getStatus().name())
                .subtotal(order.getSubtotal())
                .totalDiscount(order.getTotalDiscount())
                .total(order.getTotal())
                .totalQuantity(order.getTotalQuantity())
                .items(order.getItems().stream()
                        .map(this::mapToOrderItemResponseDTO)
                        .collect(Collectors.toList()))
                .comments(order.getComments())
                .userIp(order.getUserIp())
                .canBeCancelled(order.canBeCancelled())
                .minutesSinceCreation(minutesSinceCreation)
                .build();
    }

    private OrderSummaryDTO mapToOrderSummaryDTO(Order order) {
        return OrderSummaryDTO.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .storeId(order.getStoreId())
                .sellerName(order.getSellerName())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .totalItems(order.getItems().size())
                .totalQuantity(order.getTotalQuantity())
                .canBeCancelled(order.canBeCancelled())
                .build();
    }

    private CustomerDTO mapToCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .document(customer.getDocument())
                .documentType(customer.getDocumentType())
                .build();
    }

    private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .productHawa(item.getProductHawa())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountPercentage(item.getDiscountPercentage())
                .discountAmount(item.getDiscountAmount())
                .finalUnitPrice(item.getFinalUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

}
