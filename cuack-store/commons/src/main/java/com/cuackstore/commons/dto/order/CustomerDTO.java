package com.cuackstore.commons.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "Nombre del cliente es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String name;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 200, message = "El email no puede exceder 200 caracteres")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{0,20}$", message = "El teléfono debe tener un formato válido")
    private String phone;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String address;

    @Size(max = 50, message = "El documento no puede exceder 50 caracteres")
    private String document;

    @Size(max = 20, message = "El tipo de documento no puede exceder 20 caracteres")
    private String documentType;
}
