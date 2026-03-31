
# WishDish

Aplicación web para gestión de comandas de restaurante. Proyecto académico desarrollado con Angular y Spring Boot.

## Descripción del Proyecto

WishDish es un sistema de gestión de comandas que permite:
- Gestionar categorías de productos
- Crear y administrar productos del menú
- Gestionar mesas del restaurante
- Crear comandas asociadas a mesas
- Añadir productos a las comandas con notas personalizadas
- Seguimiento del estado de las comandas (pendiente, preparando, servida)

## Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 4.0.5**
  - Spring Data JPA
  - Spring Web MVC
- **MySQL 8.0** (base de datos)
- **Hibernate** (ORM)
- **Maven** (gestión de dependencias)

### Frontend
- **Angular 19**
- **TypeScript**
- **Node.js** y **npm**

## Estructura del Proyecto

```
PS-APP-wishdish/
├── backend/              # API REST con Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wishdish/backend/
│   │   │   │   ├── controller/    # Controladores REST
│   │   │   │   ├── service/       # Lógica de negocio
│   │   │   │   ├── repository/    # Repositorios JPA
│   │   │   │   └── entity/        # Entidades JPA
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/             # Aplicación Angular
│   ├── src/
│   └── package.json
├── database/             # Scripts SQL
│   ├── schema.sql       # Estructura de la BD
│   └── data.sql         # Datos de prueba
└── README.md
```

## Requisitos Previos

- **Java JDK 17** o superior
- **Node.js 18** o superior y **npm**
- **MySQL 8.0** o superior
- **IntelliJ IDEA** (recomendado) o cualquier IDE Java
- **Git**

## Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd PS-APP-wishdish
```

### 2. Configurar la Base de Datos

1. Crear la base de datos en MySQL:

```sql
CREATE DATABASE wishdish;
```

2. Ejecutar los scripts de inicialización:

```bash
# Crear las tablas
mysql -u root -p wishdish < database/schema.sql

# Insertar datos de prueba (opcional)
mysql -u root -p wishdish < database/data.sql
```

3. Actualizar credenciales en `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wishdish
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Instalar Dependencias del Backend

```bash
cd backend
./mvnw clean install
```

### 4. Instalar Dependencias del Frontend

```bash
cd frontend
npm install
```

## Ejecutar el Proyecto

### Backend (Puerto 8080)

**Opción 1: Con Maven**
```bash
cd backend
./mvnw spring-boot:run
```

**Opción 2: Con IntelliJ IDEA**
1. Abrir el proyecto en IntelliJ
2. Localizar la clase `BackendApplication.java`
3. Click derecho → Run 'BackendApplication'

### Frontend (Puerto 4200)

```bash
cd frontend
ng serve
```

O con npm:

```bash
npm start
```

Acceder a la aplicación en: **http://localhost:4200**

## Configuración en IntelliJ IDEA

### Importar el Proyecto

1. **File → Open** y seleccionar la carpeta raíz del proyecto
2. IntelliJ detectará automáticamente el proyecto Maven
3. Esperar a que se descarguen las dependencias

### Configurar el JDK

1. **File → Project Structure → Project**
2. Establecer **SDK: Java 17**
3. Establecer **Language level: 17**

### Configurar Spring Boot

1. **Run → Edit Configurations**
2. Click en **+** → **Spring Boot**
3. Configurar:
   - **Name**: Backend
   - **Main class**: `com.wishdish.backend.BackendApplication`
   - **Use classpath of module**: backend
4. **Apply** → **OK**

### Habilitar Hot Reload (Opcional)

1. **File → Settings → Build, Execution, Deployment → Compiler**
2. Activar **Build project automatically**
3. **Settings → Advanced Settings**
4. Activar **Allow auto-make to start even if developed application is currently running**

### Base de Datos en IntelliJ

1. **View → Tool Windows → Database**
2. Click en **+** → **Data Source** → **MySQL**
3. Configurar:
   - **Host**: localhost
   - **Port**: 3306
   - **Database**: wishdish
   - **User**: root
   - **Password**: root
4. **Test Connection** → **OK**

## Probar el Proyecto

### Probar el Backend

#### 1. Verificar que el servidor esté corriendo

Abrir el navegador en: **http://localhost:8080**

#### 2. Probar los endpoints con cURL

**Obtener todas las categorías:**
```bash
curl -X GET http://localhost:8080/api/categorias
```

**Crear una nueva categoría:**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Entrantes","descripcion":"Platos para comenzar"}'
```

**Obtener categoría por ID:**
```bash
curl -X GET http://localhost:8080/api/categorias/1
```

**Actualizar una categoría:**
```bash
curl -X PUT http://localhost:8080/api/categorias/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Entrantes Variados","descripcion":"Platos para empezar la comida"}'
```

**Eliminar una categoría:**
```bash
curl -X DELETE http://localhost:8080/api/categorias/1
```

#### 3. Probar con Postman

1. Importar la colección de endpoints (o crear una nueva)
2. Base URL: `http://localhost:8080`
3. Probar los endpoints CRUD de `/api/categorias`

#### 4. Ejecutar tests unitarios

```bash
cd backend
./mvnw test
```

### Probar el Frontend

1. Abrir el navegador en: **http://localhost:4200**
2. Verificar que la aplicación Angular carga correctamente
3. Abrir las herramientas de desarrollo (F12)
4. Verificar que no hay errores en la consola

## Endpoints Disponibles

### API de Categorías

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/categorias` | Obtener todas las categorías |
| GET | `/api/categorias/{id}` | Obtener categoría por ID |
| GET | `/api/categorias/buscar?nombre={nombre}` | Buscar categoría por nombre |
| GET | `/api/categorias/contar` | Contar total de categorías |
| POST | `/api/categorias` | Crear nueva categoría |
| PUT | `/api/categorias/{id}` | Actualizar categoría |
| DELETE | `/api/categorias/{id}` | Eliminar categoría |

## Solución de Problemas Comunes

### Error de conexión a MySQL

- Verificar que MySQL esté corriendo
- Comprobar credenciales en `application.properties`
- Verificar que la base de datos `wishdish` existe

### Puerto 8080 ya en uso

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Puerto 4200 ya en uso

```bash
ng serve --port 4201
```

### Errores de compilación en IntelliJ

1. **File → Invalidate Caches / Restart**
2. Reimportar el proyecto Maven
3. **Build → Rebuild Project**

## Autores

Proyecto académico - WishDish Team

## Documentación Adicional

- Drive del proyecto: Ver `Documentation.txt` en la raíz del repositorio
