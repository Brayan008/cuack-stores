# 🛒 Cuack Store

Este proyecto es una solución fullstack desarrollada como parte de un examen técnico para la vacante de desarrollador Fullstack. La aplicación permite gestionar pedidos de camionetas, validar disponibilidad en inventario antes de confirmar el pedido, y mantener trazabilidad mediante auditoría.

---

## 🧰 Tecnologías utilizadas

### 🔙 Backend (Java + Spring Boot)
- Java 11
- Spring Boot 2.x
- Spring Cloud (Config, Eureka, Gateway)
- Spring Data JPA (SQL Server)
- Spirng R2DBC (Persistencia en reactividad)
- Spring Security + JWT
- Spring Web Flux
- Spring Web
- Gradle
- Spring Validation

### 🌐 Frontend (React + Vite)
- React 18
- Vite
- Bootstrap
- Axios
- React Router DOM
- Redux Toolkit

---

## 🧠 Patrones de diseño aplicados

### Backend

- **Arquitectura en capas**: Controladores, servicios, repositorios y entidades.
- **DTO + Mapper Pattern**: Separación de entidades y DTOs.
- **Builder Pattern**: Uso de `@Builder` (Lombok) para facilitar construcción de objetos.
- **Service Locator**: Usando Eureka para descubrimiento de microservicios.
- **JWT Authentication**: Seguridad con tokens desde el gateway.

### Frontend

- **Container-Presentational Pattern**: 
  - `pages/` contienen la lógica de negocio.
  - `components/` renderizan la UI basada en props.

- **Hooks personalizados**:
  - `useAuth0.jsx` abstrae la lógica de autenticación.

- **Redux Toolkit Slices**:
  - Manejo de estado global desacoplado por dominio: `ordersSlice`, `productsSlice`, `authSlice`.

- **Service Layer Pattern**:
  - Abstracción de peticiones HTTP en `services/`, permitiendo desacoplar lógica de negocio del UI.

- **Modularización por dominio**:
  - Estructura entre componentes, servicios y estado para `orders` y `products`.

---

## 🏗️ Arquitectura general

El proyecto sigue un enfoque de microservicios con frontend desacoplado. El Gateway expone los servicios y controla la autenticación. La comunicación entre microservicios se maneja vía Eureka (Service Discovery). Se intento realizar via zuul pero dio problemas de incompatibilidad, otra opcion fue consul.

`
    [Usuario] ⇄ [Frontend React]
               ↓
        [Gateway Server]
               ↓
┌──────────────┴──────────────┐ -> [Config Server (obtener las properties)]
↓        ↓        ↓         ↓
Orders Service Inventory Service
              ↓ ↓
    [SQL Server Database]

`

## 📦 Estructura del proyecto
cuack-store/
├── cuack-store/         # Backend (Spring Boot microservices)
│   ├── commons/
│   ├── config-server/
│   ├── eureka-server/
│   ├── gateway-server/
│   ├── orders/
│   ├── inventory/
├── cuack-store-front/   # Frontend (React + Vite)
├── db/
│   └── quack-store.sql  # Script de carga inicial SQL Server
└── docker-compose.yml