# ShopFlow — Guía de Defensa Técnica (Docker + Swagger + Eureka + Git)

## 1. Diagnóstico de tu proyecto (antes de mis cambios)

| Elemento | Estado encontrado |
|---|---|
| Dockerfiles | ❌ No existían en ningún módulo |
| docker-compose.yml | ❌ No existía |
| Repositorio Git | ❌ No inicializado (`.git` no existe) |
| Dependencia Swagger/springdoc | ✅ Presente en los 9 microservicios (falta en gateway/eureka, que no la necesitan) |
| Anotaciones Swagger en controllers (`@Operation`, `@Tag`, etc.) | ❌ No existían — Swagger funcionaba pero con documentación genérica autogenerada |
| Eureka client | ✅ Registrado en los 9 microservicios + gateway |
| Uso real de Eureka para enrutar | ⚠️ Parcial — el Gateway y los Feign Clients usan **URLs fijas** (`localhost:puerto`), no hacen *service discovery* dinámico. Eureka solo funciona como panel de registro/salud. |
| Perfiles (`dev`/`docker`) | ❌ Solo existía `application.yml` con `localhost` fijo |

## 2. Qué agregué

- **Dockerfile** en cada uno de los 11 módulos (`eureka-server`, `api-gateway`, y los 9 `ms-*`) — build multi-stage con Maven + JRE 21 Alpine.
- **`application-docker.yml`** en cada módulo: mismo contenido que tu `application.yml`, pero con `localhost` reemplazado por el nombre del contenedor correspondiente (ej: `mysql`, `eureka-server`, `ms-producto`). Esto es justo lo que pide el indicador **IE 3.3.4** (perfiles claros dev/docker).
- **`docker-compose.yml`** en la raíz: levanta MySQL + Eureka + Gateway + los 9 microservicios en una red interna (`shopflow-net`), usando `SPRING_PROFILES_ACTIVE=docker` para activar el perfil correcto en cada contenedor.
- **`OpenApiConfig.java`** en cada microservicio: agrega título, descripción y contacto a Swagger (bean `OpenAPI`). Antes tu Swagger UI mostraba nombres genéricos; ahora cada servicio se identifica claramente.
- **`.gitignore`** y **`.dockerignore`** por módulo.

## 3. Respondiendo tus preguntas del chat de clase

**"¿Qué es Swagger y para qué sirve?"**
Es una interfaz gráfica (UI) que se genera automáticamente a partir del código, para que cualquier persona (frontend, QA, otro equipo) vea qué endpoints existen, qué reciben y qué devuelven, sin leer el código fuente. En Spring Boot se activa con la dependencia `springdoc-openapi-starter-webmvc-ui`, y queda disponible en `http://localhost:PUERTO/swagger-ui/index.html`.

**"¿Dónde se implementa Eureka?"**
Se implementa en el `pom.xml` (dependencia `spring-cloud-starter-netflix-eureka-client`) y se configura en `application.yml` con:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```
Eso hace que el microservicio se **registre** en el `eureka-server` al arrancar (lo verás en el dashboard `http://localhost:8761`). **Ojo:** en tu proyecto actual, aunque los servicios se registran, ni el Gateway ni los `FeignClient` consultan a Eureka para resolver direcciones — usan URLs fijas por `application.yml`. Debes ser honesto con esto en la defensa: Eureka funciona como registro/monitoreo, no como *discovery* activo. Si el profesor pregunta "¿por qué no usas `lb://`", la respuesta correcta es: "Eureka está implementado como registro de servicios (Service Registry), pero el enrutamiento se resuelve por configuración explícita en YAML, no por *load-balanced discovery*." Es una respuesta técnicamente correcta y defendible.

**"¿Qué otra configuración tuve que agregar para Eureka?"**
- Dependencia en `pom.xml`.
- Bloque `eureka.client.service-url.defaultZone` en cada `application.yml`.
- `eureka.instance.prefer-ip-address: true` (para que se registre con IP en vez de hostname, útil en Docker).
- El `eureka-server` en sí necesita `spring-cloud-starter-netflix-eureka-server`, `@EnableEurekaServer` en la clase principal, y `register-with-eureka: false` / `fetch-registry: false` (porque el servidor no se registra a sí mismo).

**"¿En el pom se definió que es un framework y depende de un servicio Eureka?"**
Sí — la dependencia `spring-cloud-starter-netflix-eureka-client` convierte tu app en un **cliente Eureka**; sin ella, Spring Boot no sabe qué es Eureka. El `spring-cloud-dependencies` (BOM) en `<dependencyManagement>` fija las versiones compatibles entre todos los starters de Spring Cloud (Gateway, Eureka, OpenFeign) para que no choquen entre sí.

**"Tener abierto GitHub y Swagger" / "y render" / "el eureka como se implementó" / "en el render tiene que ir eureka"**
Interpreto esto como una lista de lo que debes tener abierto y funcionando el día de la defensa:
1. GitHub (repositorio con historial de commits).
2. Swagger UI de al menos un par de microservicios.
3. Render (o Docker local) con los servicios corriendo.
4. Eureka dashboard mostrando los servicios registrados — **si despliegas en Render, el `eureka-server` también debe estar desplegado ahí** (como un servicio más), y los demás microservicios deben apuntar su `defaultZone` a la URL pública de ese Eureka en Render, no a `localhost`. Localmente ya lo resolví con el perfil `docker`; si vas a Render, es la misma idea pero con la URL pública que te entrega la plataforma en vez del nombre del contenedor.

## 4. Cómo levantar todo con Docker (local)

Desde la carpeta raíz `ShopFlow/`:

```bash
docker compose up --build
```

Esto construye las 11 imágenes y las levanta en orden razonable. Verifica:
- Eureka: http://localhost:8761
- Gateway: http://localhost:8090
- Swagger de un servicio: http://localhost:8082/swagger-ui/index.html
- MySQL queda expuesto en el puerto 3306 con todas las bases creadas automáticamente (usa tu `crear_bases_de_datos.sql`).

Para bajar todo:
```bash
docker compose down
```
Para bajar todo y borrar los datos de MySQL:
```bash
docker compose down -v
```

**Antes del sábado, pruébalo tú mismo al menos una vez completo** — así puedes explicar con seguridad cada paso si te preguntan (ver requisito: *"Si no puedes ejecutar correctamente la aplicación... se asumirá que no participaste"*).

## 5. Cómo dejar el repositorio Git en orden

Tu proyecto no tenía `.git`. Recomendación de commits (según rúbrica **IE 2.5.1**, pide commits técnicos, progresivos y distribuidos):

```bash
cd ShopFlow
git init
git add .gitignore README.md
git commit -m "chore: agrega gitignore y estructura inicial del README"

git add eureka-server/
git commit -m "feat(eureka-server): agrega Dockerfile y perfil docker"

git add api-gateway/
git commit -m "feat(api-gateway): agrega Dockerfile y perfil docker con rutas hacia contenedores"

git add ms-producto/ ms-inventario/
git commit -m "feat(ms-producto,ms-inventario): agrega Dockerfile, perfil docker y OpenApiConfig"

# repite por cada microservicio o agrúpalos en 2-3 commits temáticos más
git add docker-compose.yml
git commit -m "feat(infra): agrega docker-compose para levantar todo el ecosistema"

git branch -M main
git remote add origin https://github.com/TU_USUARIO/ShopFlow.git
git push -u origin main
```

**Importante (regla de la rúbrica):** una vez que subas la versión definitiva a evaluar, **no vuelvas a hacer push** — cualquier cambio, aunque sea mínimo, después de la fecha de entrega y antes de la evaluación te deja con nota 1.0 automática, individual y grupal.

## 6. Swagger — cómo dejarlo con mejor nivel (no solo "aceptable")

Ya agregué `OpenApiConfig.java` (título/descripción por servicio). Para subir de "aceptable" a "muy buen desempeño" en **IE 3.2.1**, agrega anotaciones en al menos los endpoints principales de 2-3 controllers. Ejemplo para `ProductoController`:

```java
@Tag(name = "Productos", description = "Operaciones sobre el catálogo de productos")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    @Operation(summary = "Lista todos los productos", description = "Retorna el catálogo completo de productos disponibles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @Operation(summary = "Obtiene un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    // ...
}
```
Imports necesarios: `io.swagger.v3.oas.annotations.Operation`, `io.swagger.v3.oas.annotations.tags.Tag`, `io.swagger.v3.oas.annotations.responses.ApiResponse`, `io.swagger.v3.oas.annotations.responses.ApiResponses`.

No necesitas hacerlo en los 9 servicios con todos los endpoints — prioriza 2-3 servicios que domines bien para la defensa (ver punto 7).

## 7. Checklist de estudio priorizado para el sábado (según ponderación de la rúbrica)

Prioriza en este orden porque son los ítems de mayor peso en la **Defensa (60%)**:

1. **IE 3.1.3 (13%)** — Debes poder crear una prueba unitaria nueva EN VIVO, con mocks y Given-When-Then, sin ayuda. Practica esto en 1-2 servicios (ej. `PagoServiceTest` que ya tienes de base).
2. **IE 3.3.7 (10%)** — Debes poder levantar todo tú solo (local o Docker) sin apoyo del compañero.
3. **IE 3.1.2 (7%)** — Explicar tus pruebas unitarias: qué regla de negocio valida cada una, qué simula el mock, por qué.
4. **IE 2.2.2, IE 2.4.2, IE 2.5.3, IE 3.2.2 (5% c/u)** — Explicar tu propio código, tus commits, tu consumo REST vía Feign, y la documentación Swagger.
5. **IE 3.3.6 (6%)** — Explicar el proceso de despliegue: qué hace el Dockerfile, qué hace docker-compose, cómo se conectan los contenedores por nombre en vez de `localhost`.
6. **IE 3.3.5 (4%)** — Explicar diferencia entre `application.yml` y `application-docker.yml`, y por qué existen perfiles.

Para cada microservicio que hayas construido tú (no solo tu compañero), asegúrate de poder explicar: su Controller, su Service (reglas de negocio), su Repository, su/sus Feign Client(s), su test unitario, y su Dockerfile.
