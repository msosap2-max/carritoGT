# CarritoGT - Backend

API REST para la tienda en línea CarritoGT, desarrollada con Spring Boot y MariaDB. Es el backend del proyecto; el frontend vive en un repositorio aparte y consume esta API.

## Tecnologías

- Java 21
- Spring Boot 3.5.0 (Web, Data JPA, Validation)
- MariaDB
- Maven
- Lombok
- JWT (jjwt) para el token de inicio de sesión
- BCrypt (spring-security-crypto) para comparar contraseñas cifradas

## Requisitos

- JDK 21
- Maven (o el que incluye IntelliJ)
- MariaDB en ejecución en el puerto 3306

## Base de datos

La base se llama `tienda_db` y tiene cuatro tablas:

| Tabla | Descripción |
|---|---|
| usuarios | Cuentas de clientes y administradores |
| productos | Catálogo de la tienda |
| ordenes | Encabezado de cada compra |
| orden_detalle | Productos y cantidades de cada compra |

Para crearla, ejecuta el script `carritogtdb.sql` en MariaDB. La aplicación no crea ni modifica tablas por su cuenta.

## Configuración

Edita `src/main/resources/application.properties` con tus datos:

```properties
spring.application.name=carritogt
spring.datasource.url=jdbc:mariadb://localhost:3306/tienda_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CLAVE
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none

jwt.secret=UNA_CLAVE_LARGA_DE_AL_MENOS_32_CARACTERES
jwt.expiracion-minutos=120
server.error.include-message=always
```

No subas claves reales al repositorio.

## Ejecución

Desde IntelliJ, ejecuta la clase `CarritogtApplication`. También se puede ejecutar desde la terminal:

```
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Endpoints

### Productos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | /api/productos | Lista los productos activos | 200 |
| GET | /api/productos/{id} | Obtiene un producto | 200 / 404 |
| POST | /api/productos | Crea un producto | 201 / 400 |
| PUT | /api/productos/{id} | Actualiza todos los campos de un producto | 200 / 400 / 404 |
| DELETE | /api/productos/{id} | Desactiva un producto (activo = 0) | 204 / 404 |

El DELETE no borra la fila. Solo marca el producto como inactivo, para conservar el historial de las órdenes.

Ejemplo de cuerpo para POST y PUT:

```json
{
  "nombre": "Cargador rapido 20W",
  "descripcion": "Cargador de pared con entrada USB-C",
  "precio": 95.50,
  "stock": 35,
  "imagenUrl": "https://picsum.photos/200",
  "activo": true
}
```

### Autenticación

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | /api/auth/login | Inicia sesión y devuelve un token | 200 / 401 |

Cuerpo de la petición:

```json
{
  "email": "usuario@correo.com",
  "password": "su_clave"
}
```

Respuesta exitosa:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": 2,
    "nombre": "Ana Lopez",
    "email": "ana.lopez@correo.com",
    "rol": "cliente"
  }
}
```

Las contraseñas se guardan cifradas con BCrypt en la columna `contrasena_hash` y nunca se devuelven en las respuestas.

## CORS

La API acepta peticiones desde `http://localhost:3000`, donde corre el frontend en desarrollo. Para cambiarlo, edita la clase `WebConfig`.

## Estructura

```
src/main/java/org/umg/carritogt/
    CarritogtApplication.java
    AuthController.java
    JwtService.java
    WebConfig.java
    Producto.java
    ProductoController.java
    ProductoRepository.java
    Usuario.java
    UsuarioRepository.java
```

Cada tabla se representa con una entidad (la ficha de datos), un repositorio (acceso a la base) y un controlador (los endpoints).
