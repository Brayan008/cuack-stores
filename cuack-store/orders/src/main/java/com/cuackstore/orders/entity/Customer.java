package com.cuackstore.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Column(name = "customer_name", nullable = false, length = 200)
    @NotBlank(message = "Nombre del cliente es requerido")
    private String name;

    @Column(name = "customer_email", length = 200)
    private String email;

    @Column(name = "customer_phone", length = 20)
    private String phone;

    @Column(name = "customer_address", length = 500)
    private String address;

    @Column(name = "customer_document", length = 50)
    private String document;

    @Column(name = "customer_document_type", length = 20)
    private String documentType;
}
