package com.cuackstore.orders.service;

import com.cuackstore.commons.dto.order.AvailabilityResponseDTO;
import com.cuackstore.commons.dto.order.InventoryProductDTO;

public interface InventoryService {
    InventoryProductDTO getProduct(String hawa);
    AvailabilityResponseDTO checkAvailability(String hawa);
    boolean decrementStock(String hawa, Integer quantity);
    boolean incrementStock(String hawa, Integer quantity);
}
