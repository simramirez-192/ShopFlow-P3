# ShopFlow - Arquitectura de Microservicios

## Integrantes
- Jorge Delgado
- Simón Ramírez

## Descripción
ShopFlow es un sistema de e-commerce construido con arquitectura de microservicios independientes. Cada servicio tiene su propia base de datos MySQL y se comunica con otros mediante Feign Client (REST). El sistema cubre el ciclo completo de compra: autenticación, gestión de usuarios y productos, inventario, carrito, órdenes, pagos, envíos, notificaciones y reseñas.

## Microservicios

| Microservicio    | Puerto | Base de Datos              | Descripción                        |
|------------------|--------|----------------------------|------------------------------------|
| eureka-server    | 8761   | -                          | Registro y descubrimiento de servicios |
| api-gateway      | 8090   | -                          | Enrutamiento centralizado          |
| ms-autenticacion | 8080   | shopflow_autenticacion     | Registro y login de credenciales   |
| ms-usuario       | 8081   | shopflow_usuario           | Gestión de perfiles de usuario     |
| ms-producto      | 8082   | shopflow_producto          | Catálogo de productos y categorías |
| ms-inventario    | 8083   | shopflow_inventario        | Control de stock y movimientos     |
| ms-carrito       | 8084   | shopflow_carrito           | Carrito de compras por usuario     |
| ms-orden         | 8085   | shopflow_orden             | Creación y gestión de órdenes      |
| ms-pago          | 8086   | shopflow_pago              | Procesamiento de pagos             |
| ms-envio         | 8087   | shopflow_envio             | Seguimiento de envíos              |
| ms-notificacion  | 8088   | shopflow_notificacion      | Notificaciones a usuarios          |
| ms-resena        | 8089   | shopflow_resena            | Reseñas y calificaciones           |

## Rutas del Gateway (puerto 8090)

| Prefijo                  | Microservicio    |
|--------------------------|------------------|
| /api/autenticacion/**    | ms-autenticacion |
| /api/usuarios/**         | ms-usuario       |
| /api/productos/**        | ms-producto      |
| /api/categorias/**       | ms-producto      |
| /api/inventario/**       | ms-inventario    |
| /api/carrito/**          | ms-carrito       |
| /api/ordenes/**          | ms-orden         |
| /api/pagos/**            | ms-pago          |
| /api/envios/**           | ms-envio         |
| /api/notificaciones/**   | ms-notificacion  |
| /api/resenas/**          | ms-resena        |

## Documentación Swagger (local)

| Microservicio    | URL Swagger UI                                      |
|------------------|-----------------------------------------------------|
| ms-autenticacion | http://localhost:8080/swagger-ui/index.html         |
| ms-usuario       | http://localhost:8081/swagger-ui/index.html         |
| ms-producto      | http://localhost:8082/swagger-ui/index.html         |
| ms-inventario    | http://localhost:8083/swagger-ui/index.html         |
| ms-carrito       | http://localhost:8084/swagger-ui/index.html         |
| ms-orden         | http://localhost:8085/swagger-ui/index.html         |
| ms-pago          | http://localhost:8086/swagger-ui/index.html         |
| ms-envio         | http://localhost:8087/swagger-ui/index.html         |
| ms-notificacion  | http://localhost:8088/swagger-ui/index.html         |
| ms-resena        | http://localhost:8089/swagger-ui/index.html         |

## Tecnologías

- Java 21
- Spring Boot 3.3.4
- Spring Cloud 2023.0.3 (Eureka + Gateway + Feign)
- Spring Data JPA + Hibernate
- Spring Cloud Gateway
- Netflix Eureka
- OpenFeign
- Springdoc OpenAPI 2.6.0 (Swagger)
- JUnit 5 + Mockito
- MySQL (Laragon)
- Maven

## Ejecución Local

### Prerrequisitos

- Java 21
- Laragon con MySQL corriendo en el puerto 3306
- IntelliJ IDEA o VS Code con Spring Boot Extension Pack
- Maven 3.9+

### 1. Crear las bases de datos

Ejecutar el archivo `crear_bases_de_datos.sql` en MySQL:

```sql
-- En Laragon: HeidiSQL o MySQL Shell
source crear_bases_de_datos.sql
```

### 2. Orden de arranque

```
1. eureka-server     (puerto 8761) ← iniciar primero siempre
2. ms-autenticacion  (puerto 8080)
3. ms-usuario        (puerto 8081)
4. ms-producto       (puerto 8082)
5. ms-inventario     (puerto 8083)
6. ms-carrito        (puerto 8084)
7. ms-orden          (puerto 8085)
8. ms-pago           (puerto 8086)
9. ms-envio          (puerto 8087)
10. ms-notificacion  (puerto 8088)
11. ms-resena        (puerto 8089)
12. api-gateway      (puerto 8090) ← iniciar al final
```

Para cada módulo:

```bash
cd <nombre-del-modulo>
mvn spring-boot:run
```

### 3. Verificar Eureka

Una vez levantado el eureka-server, ingresar a:

```
http://localhost:8761
```

Todos los microservicios deben aparecer registrados en el panel.

### 4. Ejecutar pruebas unitarias

```bash
cd ms-usuario
mvn test

cd ms-orden
mvn test

cd ms-pago
mvn test
```

## Comunicación entre Microservicios

```
ms-autenticacion → consulta ms-usuario
ms-usuario       → consulta ms-autenticacion
ms-inventario    → consulta ms-producto
ms-carrito       → consulta ms-producto, ms-usuario
ms-orden         → consulta ms-usuario, ms-producto, ms-inventario, ms-notificacion
ms-pago          → consulta ms-orden, ms-notificacion
ms-envio         → consulta ms-orden, ms-notificacion
ms-notificacion  → consulta ms-usuario
ms-resena        → consulta ms-producto, ms-usuario, ms-orden
```

## Estructura de Paquetes (Patrón CSR)

```
com.shopflow.ms{nombre}/
├── controller/    → Endpoints REST
├── service/       → Lógica de negocio
├── repository/    → Acceso a datos (JpaRepository)
├── model/         → Entidades JPA
├── dto/           → RequestDTO y ResponseDTO
├── exception/     → GlobalExceptionHandler
├── client/        → Feign Clients (comunicación entre servicios)
└── config/        → DataInitializer
```
