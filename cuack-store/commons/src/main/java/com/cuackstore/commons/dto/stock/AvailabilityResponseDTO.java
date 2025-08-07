package com.cuackstore.commons.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDTO {
    private String hawa;
    private Boolean available;
    private Integer stock;
    private String message;

    public static AvailabilityResponseDTO available(String hawa, Integer stock) {
        return AvailabilityResponseDTO.builder()
                .hawa(hawa)
                .available(true)
                .stock(stock)
                .message("Producto disponible")
                .build();
    }

    public static AvailabilityResponseDTO unavailable(String hawa, String reason) {
        return AvailabilityResponseDTO.builder()
                .hawa(hawa)
                .available(false)
                .stock(0)
                .message(reason)
                .build();
    }
}