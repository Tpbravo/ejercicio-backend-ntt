#  Microservicios Bancarios – Prueba Técnica

##  Descripción General

Implementación de una **arquitectura de microservicios bancarios** que gestiona clientes, cuentas y movimientos.

Desarrollada con **Spring Boot**, **Kafka**, **JPA/Hibernate** y **PostgreSQL**, aplicando principios de **Clean Architecture**, **DDD** y buenas prácticas como el uso de **Repository Pattern**, **DTOs**, **manejadores globales de excepciones**, **pruebas automatizadas** y **contenedorización con Docker**.

Cumple las funcionalidades **F1–F6** establecidas en la prueba técnica.

---

## Arquitectura General

El sistema se divide en **dos microservicios independientes**:

- **Gestión de Clientes** → manejo de `Persona` y `Cliente`
- **Operaciones Bancarias** → manejo de `Cuenta` y `Movimiento`

Ambos se comunican **asíncronamente** mediante **Apache Kafka** para la publicación de eventos entre microservicios.

Los servicios usan **bases de datos PostgreSQL separadas** y se orquestan a través de **Docker Compose** junto al broker Kafka.

---
---
## Nota sobre Recursos (Docker Compose)

Los contenedores están configurados con límites de CPU y memoria elevados para optimizar los tiempos de arranque de Spring Boot y Kafka.

Para equipos con recursos limitados, puedes ajustar en `docker-compose.yml`:
```yaml
deploy:
  resources:
    limits:
      cpus: "3.0"
      memory: 4g
    reservations:
      cpus: "1.0"
      memory: 1g
```

Estos valores garantizan compatibilidad con equipos de al menos 8 GB de RAM sin afectar la funcionalidad.
---
--
## Validaciones en Endpoints

Cada endpoint aplica **validaciones a nivel de DTO** usando **Jakarta Bean Validation**, asegurando la integridad de datos antes de llegar a la capa de dominio.

### Ejemplo: `CuentaDTO`

Controla los campos obligatorios y sus reglas de negocio:

```java
@Data
public class CuentaDTO {
  @NotBlank(message = "{numeroCuenta.notblank}", groups = OnCreate.class)
  @Size(max = 20, message = "{numeroCuenta.size}")
  private String numeroCuenta;

  @NotNull(message = "{tipoCuenta.notnull}")
  private TipoCuentaEnum tipoCuenta;

  @NotNull(message = "{saldoInicial.notnull}")
  @PositiveOrZero(message = "{saldoInicial.positiveOrZero}")
  private BigDecimal saldoInicial;

  @NotNull(message = "{estado.notnull}")
  private Boolean estado;

  @NotNull(message = "{clienteId.notnull}")
  private String clienteId;
}
```

### Documentación API

La colección de Postman incluida documenta detalladamente cada endpoint (métodos, body, respuestas esperadas y validaciones).

Además, el archivo `environment.json` contiene las URLs configuradas de los microservicios:

| Variable | Descripción | URL |
|----------|-------------|-----|
| `host_clientes` | Microservicio de gestión de clientes  | `http://localhost:8081/api/gestion-clientes` |
| `host_operaciones` | Microservicio de operaciones bancarias | `http://localhost:8082/api/operaciones-bancarias` |

---

## Manejo Centralizado de Errores

Todos los errores del sistema se manejan de forma centralizada con `GlobalExceptionHandler` (`@ControllerAdvice`).

### Escenarios controlados:

- Errores de validación (`MethodArgumentNotValidException`, `ConstraintViolationException`)
- Errores de integridad (`DataIntegrityViolationException`)
- Excepciones de negocio (`RuntimeException`)
- Recursos no encontrados (`ResourceNotFoundException`)
- Errores generales (`Exception`)

### Estructura de respuesta

Cada respuesta devuelve un objeto `ApiError` con los campos:

```json
{
  "timestamp": "2023-10-28T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": ["Field 'numeroCuenta' is required"],
  "path": "/api/cuentas"
}
```

---

## Internacionalización (i18n)

El sistema soporta internacionalización de mensajes (`es`/`en`), ubicados en:

- `ValidationMessages_es.properties`
- `ValidationMessages_en.properties`

Los textos se seleccionan dinámicamente según el `Locale` actual, gestionado por `MessageSource`.

---

## Pruebas Automatizadas

Cada microservicio incluye múltiples pruebas unitarias y de integración, organizadas por capas:

| Tipo de prueba | Clase | Descripción |
|----------------|-------|-------------|
| **Controlador** | `ClienteControllerTest` `CuentaControllerTest.java` ... | Valida endpoints REST con `MockMvc` |
| **Servicio** | `ClienteServiceTests` `ClienteServiceTests` ... | Prueba la lógica de negocio y eventos Kafka |
| **Dominio** | `ClienteTests` `CuentaTests.class` ... | Valida reglas y anotaciones de entidad |
| **Repositorio JPA** | `ClienteRepositoryIntegrationTest` `MovimientoRepositoryIntegrationTest.class` ... | Prueba persistencia y restricciones con H2 |
| **Kafka** | `ClienteEventProducerTest` `ClienteEventListenerTest.class` | Verifica publicación de eventos con `EmbeddedKafka` |

---

## Estructura de Base de Datos

El archivo `BaseDatos.sql` define la creación de roles, bases y configuración regional:

### Bases de datos separadas:

- `gestion_clientes_db` → microservicio clientes
- `operaciones_bancarias_db` → microservicio cuentas/movimientos

### Roles:

- `ms_clientes`
- `ms_operaciones`
- `report_readonly`

### Configuración:

- **ICU_LOCALE**: `es-ES` (ordenamiento y acentos correctos)
- **Zona horaria**: `America/Guayaquil`
- **Extensión**: `uuid-ossp` habilitada en ambas bases

---

## Despliegue y Ejecución

### Requisitos

- Docker y Docker Compose
- Postman v9.13.2+

### Ejecución

```bash
docker-compose up -d
```

### Servicios disponibles:

- **Gestión Clientes**: http://localhost:8081
- **Operaciones Bancarias**: http://localhost:8082
- **Kafka Broker**: localhost:9092

### Validación con Postman

1. Importar la colección y el environment incluidos
2. Flujo sugerido:
   - Crear Cliente
   - Crear Cuenta
   - Registrar Movimiento
   -  Generar Reporte por rango de fechas

---

## Tecnologías Utilizadas

- ☕ **Java 17** / **Spring Boot 3**
- **Spring Data JPA** / **Hibernate**
- **Apache Kafka**
- **PostgreSQL**
- **Docker** & **Docker Compose**
- **JUnit 5** / **Mockito** / **Embedded Kafka** / **H2**
- **Postman** (colección + environment)

---



## Autor

**Paul Alexander Bravo Perez**  
Prueba Técnica – Arquitectura de Microservicios

---
