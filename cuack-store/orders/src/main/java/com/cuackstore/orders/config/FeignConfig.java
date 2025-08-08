package com.cuackstore.orders.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Configurar el nivel de logging para Feign
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Decoder personalizado para errores de Feign
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            HttpStatus status = HttpStatus.valueOf(response.status());

            log.error("Error en Feign Client - Método: {}, Status: {}, Razón: {}",
                    methodKey, status, response.reason());

            switch (status) {
                case NOT_FOUND:
                    return new RuntimeException("Recurso no encontrado");
                case BAD_REQUEST:
                    return new RuntimeException("Petición inválida");
                case INTERNAL_SERVER_ERROR:
                    return new RuntimeException("Error interno del servidor");
                case SERVICE_UNAVAILABLE:
                    return new RuntimeException("Servicio no disponible temporalmente");
                default:
                    return new RuntimeException("Error de comunicación: " + response.reason());
            }
        };
    }
}
