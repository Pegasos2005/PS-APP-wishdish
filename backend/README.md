# Backend - WishDish

API REST para la aplicación WishDish construida con Spring Boot.

## Tecnologías

- Java 17 (Temurin)
- Spring Boot 4.0.5
- Spring Data JPA
- MySQL 8.0
- Maven

## Configuración Actual

### Base de Datos

El proyecto está configurado para usar:
- **Base de datos:** wishdish
- **Usuario:** root
- **Contraseña:** root
- **Puerto:** 3306 (MySQL por defecto)

Ver configuración completa en `src/main/resources/application.properties`

Si necesitas usar otras credenciales, edita ese archivo.

### Crear la Base de Datos

```sql
CREATE DATABASE wishdish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Instalación

```bash
cd backend
./mvnw clean install
```

## Ejecución

```bash
./mvnw spring-boot:run
```

El servidor arrancará en: **http://localhost:8080**

## Población de Datos

### Funcionamiento Automático

Al iniciar el backend, se ejecuta automáticamente la clase `DataLoader.java` que carga:
- 5 categorías
- 19 productos

**Importante:** DataLoader solo carga datos si la base de datos está vacía. Si ya hay datos, no se vuelven a cargar.

### Verificar si la BD tiene datos

```sql
USE wishdish;
SELECT COUNT(*) FROM categorias;  -- Debe ser 5
SELECT COUNT(*) FROM productos;   -- Debe ser 19
```

### Resetear y recargar datos

Para borrar todos los datos y que se ejecute la carga automática de nuevo:

**Opción 1: Borrar datos**
```sql
USE wishdish;
DELETE FROM productos;
DELETE FROM categorias;
```

**Opción 2: Recrear la BD completa**
```sql
DROP DATABASE wishdish;
CREATE DATABASE wishdish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Después reinicia el backend y los datos se cargarán automáticamente.

## Gestión del Esquema de BD

Este proyecto usa **JPA/Hibernate** para gestionar las tablas automáticamente:

- Las tablas se crean desde las entidades Java en `src/main/java/com/wishdish/backend/entity/`
- La configuración `spring.jpa.hibernate.ddl-auto=update` sincroniza el esquema automáticamente
- **No ejecutes scripts SQL** para crear o modificar tablas

### Añadir o modificar tablas

1. Crear o editar la clase `@Entity` correspondiente
2. Reiniciar la aplicación
3. Hibernate aplicará los cambios automáticamente

## Estructura del Proyecto

```
backend/
├── src/main/java/com/wishdish/backend/
│   ├── controller/          # REST API endpoints
│   │   ├── MenuController.java
│   │   ├── CategoriaController.java
│   │   └── ProductoController.java
│   ├── service/             # Lógica de negocio
│   │   ├── MenuService.java
│   │   ├── CategoriaService.java
│   │   └── ProductoService.java
│   ├── repository/          # Acceso a datos (Spring Data JPA)
│   │   ├── CategoriaRepository.java
│   │   └── ProductoRepository.java
│   ├── entity/              # Entidades JPA (definen las tablas)
│   │   ├── Categoria.java
│   │   └── Producto.java
│   ├── dto/                 # DTOs para respuestas
│   │   └── MenuCategoriaDTO.java
│   ├── config/              # Configuración
│   │   ├── WebConfig.java   # CORS
│   │   └── DataLoader.java  # Población automática de datos
│   └── BackendApplication.java
└── src/main/resources/
    └── application.properties  # Configuración
```

## API Endpoints

**Base URL:** http://localhost:8080

### Menú Completo

- `GET /api/menu` - Menú completo (categorías con productos)
- `GET /api/menu/disponibles` - Solo productos disponibles
- `GET /api/menu/categoria/{id}` - Productos de una categoría
- `GET /api/menu/categoria/{id}/disponibles` - Productos disponibles de una categoría

### Categorías

- `GET /api/categorias` - Listar todas
- `GET /api/categorias/{id}` - Obtener por ID
- `POST /api/categorias` - Crear nueva
- `PUT /api/categorias/{id}` - Actualizar
- `DELETE /api/categorias/{id}` - Eliminar
- `GET /api/categorias/buscar?nombre={nombre}` - Buscar por nombre
- `GET /api/categorias/contar` - Contar total

### Productos

- `GET /api/productos` - Listar todos
- `GET /api/productos/disponibles` - Solo disponibles
- `GET /api/productos/{id}` - Obtener por ID
- `GET /api/productos/categoria/{id}` - Por categoría
- `POST /api/productos` - Crear nuevo
- `PUT /api/productos/{id}` - Actualizar
- `PATCH /api/productos/{id}/disponibilidad?disponible={true/false}` - Cambiar disponibilidad
- `DELETE /api/productos/{id}` - Eliminar
- `GET /api/productos/buscar?nombre={nombre}` - Buscar por nombre
- `GET /api/productos/contar` - Contar total
- `GET /api/productos/contar/categoria/{id}` - Contar por categoría

### Ejemplos de Uso

**Obtener menú completo:**
```bash
curl http://localhost:8080/api/menu
```

**Crear una categoría:**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ensaladas","descripcion":"Ensaladas frescas"}'
```

**Crear un producto:**
```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ensalada César",
    "descripcion": "Lechuga, pollo, parmesano",
    "precio": 9.50,
    "imagen": "assets/ensalada_cesar.jpg",
    "disponible": true,
    "categoria": {"id": 1}
  }'
```

## Modelo de Datos

```
Categoria (1) ──── (*) Producto

Categoria:
- id (PK)
- nombre
- descripcion
- fechaCreacion
- fechaActualizacion

Producto:
- id (PK)
- nombre
- descripcion
- precio
- imagen
- disponible
- categoriaId (FK)
- fechaCreacion
- fechaActualizacion
```

## Testing

```bash
./mvnw test
```

## Solución de Problemas

### Error de conexión a MySQL

- Verificar que MySQL esté corriendo
- Verificar que la BD `wishdish` exista
- Comprobar credenciales en `application.properties`

### Puerto 8080 ocupado

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Tablas no se crean

- Verificar que `spring.jpa.hibernate.ddl-auto=update` en `application.properties`
- Revisar logs del arranque de Spring Boot

### Datos no se cargan

- Verificar que las tablas estén vacías
- Revisar el log del backend al arrancar (debe mostrar "🔄 Cargando datos...")
- Verificar que `DataLoader.java` existe en `src/main/java/.../config/` y tiene el `@Bean` activo

## CORS

El backend está configurado para aceptar peticiones desde:
- `http://localhost:4200` (frontend en desarrollo)

Si cambias el puerto del frontend, actualiza `WebConfig.java`
