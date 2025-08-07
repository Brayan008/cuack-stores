package com.cuackstore.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private String message;
    private T data;
    private String timestamp;

    public static <T> ApiResponseDTO<T> handleBuild(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

}
