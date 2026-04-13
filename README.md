# WishDish

Aplicación web para gestión de menú de restaurante. Sistema para visualizar productos organizados por categorías.

## Estado Actual del Proyecto

**Funcionalidades implementadas:**
- Vista de menú con productos organizados por categorías
- Carga automática de datos de prueba desde la base de datos
- 5 categorías: Entrantes, Hamburguesas, Guarniciones, Postres, Bebidas
- 19 productos distribuidos en las categorías
- API REST completa para menú, categorías y productos

## Tecnologías

**Backend:**
- Java 17 + Spring Boot 4.0.5
- MySQL 8.0
- JPA/Hibernate (gestión automática de esquema)

**Frontend:**
- Angular 19
- TypeScript

## Requisitos

- Java JDK 17 temurin
- Node.js 18 
- MySQL 8.0 

## Configuración Inicial

### 1. Base de Datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE wishdish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

La aplicación está configurada para usar:
- **Usuario:** root
- **Contraseña:** root
- **Base de datos:** wishdish

Si necesitas cambiar estas credenciales, edita `backend/src/main/resources/application.properties`

### 2. Instalación de Dependencias

**Backend:**
```bash
cd backend
./mvnw clean install
```

**Frontend:**
```bash
cd frontend
npm install
```

## Configurar el acceso público mediante la red privada

### Backend
En la carpeta /config, el archivo CorsConfig, cambiar en la línea .allowedOrigins("http://192.168.68.102:4200", "http://localhost:4200")
la IP asignada por la ip del PC que soportará la app, manteniendo el puerto. -> allowedOrigins("http://192.168.x.x:4200", "http://localhost:4200")

Para saber la dirección ip asignada de tu dispositivo, ejecutar en la terminal:
> En Mac:
```bash
ifconfig
```
> En Windows:
```bash
ipconfig
```
> En Linux:
```bash
ip a
```

Con esto, lo que hacemos es que el frontend se comunique con el backend a través de la red. Lo que permite que si abres el front
desde otro dispositivo, pueda recibir los mensajes por el mismo puerto que el front del propio PC o portátil.

## Frontend
Dentro de la carpeta /src/environmets, modifica el archivo environmet.ts. La línea apiUrl: 'http://192.168.68.102:8080/api/' cambia la ip por
la del equipo local, como en el apartado anterior.

Esto permite que los usuarios que el frontend reciba y mande los datos con el backend a través del puerto del servidor, y no del localhost.

## Configuración
> Para Windows:
En el menú de configuración, entra en Red e Internet -> Propiedades de tu WiFi. Y habilitado en Privada
Luego busca en el menú de inicio: "Permitir una aplicación a través del Firewall de Windows". Pulsa "Cambiar la configuración" arriba a la derecha
y busca "Java(TM) Platform SE binary" o "OpenJDK Platform binary" y marca tanto la casilla privada como pública

Y por último ejecuta en la PowerShell *Como administrador*
```bash
New-NetFirewallRule -DisplayName "App Local" -Direction Inbound -Protocol TCP -LocalPort 8080,4200 -Action Allow
```

## Ejecutar la Aplicación

### Backend (puerto 8080)

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend (puerto 4200)

```bash
cd frontend
ng serve --host 0.0.0.0
```

**Acceder a:** http://localhost:4200

## Gestión de Datos de la Base de Datos

### Población Automática

Al iniciar el backend por primera vez, se ejecuta automáticamente la clase `DataLoader.java` que carga:
- 5 categorías
- 19 productos

El backend detecta si ya hay datos y NO los vuelve a cargar en inicios posteriores.

### Borrar Todos los Datos

Si necesitas resetear la base de datos y que se ejecute la población de nuevo:

**Opción 1: Desde MySQL**
```sql
USE wishdish;
DELETE FROM productos;
DELETE FROM categorias;
```

**Opción 2: Borrar y recrear toda la BD**
```sql
DROP DATABASE wishdish;
CREATE DATABASE wishdish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Después de borrar los datos, al reiniciar el backend se ejecutará automáticamente el `DataLoader`.

### Verificar el Estado de la BD

```sql
USE wishdish;

-- Ver cantidad de datos
SELECT COUNT(*) FROM categorias;  -- Debe ser 5
SELECT COUNT(*) FROM productos;   -- Debe ser 19

-- Ver todos los datos
SELECT * FROM categorias;
SELECT * FROM productos;
```

## Gestión del Esquema de Base de Datos

> _[!IMPORTANT]_
> Este proyecto usa JPA/Hibernate para gestionar las tablas automáticamente.

- Las tablas se crean/actualizan desde las entidades Java en `backend/src/main/java/com/wishdish/backend/entity/`
- **NO ejecutes scripts SQL** para crear o modificar tablas
- La configuración `spring.jpa.hibernate.ddl-auto=update` mantiene el esquema sincronizado

### Añadir o Modificar Tablas

1. Crear o editar la clase `@Entity` correspondiente en el paquete `entity`
2. Reiniciar la aplicación
3. Hibernate aplicará los cambios automáticamente

## API REST - Endpoints Principales

**Base URL:** http://localhost:8080

### Menú Completo
- `GET /api/menu` - Menú completo (categorías con sus productos)
- `GET /api/menu/disponibles` - Solo productos disponibles

### Categorías
- `GET /api/categorias` - Listar todas
- `GET /api/categorias/{id}` - Obtener por ID
- `POST /api/categorias` - Crear nueva
- `PUT /api/categorias/{id}` - Actualizar
- `DELETE /api/categorias/{id}` - Eliminar

### Productos
- `GET /api/productos` - Listar todos
- `GET /api/productos/{id}` - Obtener por ID
- `GET /api/productos/categoria/{id}` - Productos de una categoría
- `POST /api/productos` - Crear nuevo
- `PUT /api/productos/{id}` - Actualizar
- `PATCH /api/productos/{id}/disponibilidad?disponible=true` - Cambiar disponibilidad
- `DELETE /api/productos/{id}` - Eliminar

**Ejemplo de prueba:**
```bash
curl http://localhost:8080/api/menu
```

## Estructura del Proyecto

```
PS-APP-dishWish/
├── backend/
│   ├── src/main/java/com/wishdish/backend/
│   │   ├── controller/     # Endpoints REST
│   │   ├── service/        # Lógica de negocio
│   │   ├── repository/     # Acceso a datos (JPA)
│   │   ├── entity/         # Entidades JPA (definen las tablas)
│   │   └── dto/            # DTOs para respuestas
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   └── src/app/
│       ├── menu/           # Componente principal del menú
│       ├── models/         # Interfaces TypeScript
│       └── services/       # Servicios para comunicación con API
└── README.md
```

## Solución de Problemas

### Error de conexión a MySQL
- Verificar que MySQL esté corriendo
- Verificar que la BD `wishdish` exista
- Comprobar credenciales (root/root por defecto)

### Puerto 8080 ocupado
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Puerto 4200 ocupado
```bash
ng serve --port 4201
```

## Para Desarrolladores

### Flujo de Trabajo Recomendado

1. **Asegurar que la BD tenga datos:** Verificar con las consultas SQL de arriba
2. **Iniciar backend:** Debe mostrar el mensaje de conexión exitosa a MySQL
3. **Iniciar frontend:** Debe cargar los productos desde la API
4. **Verificar en navegador:** http://localhost:4200 debe mostrar el menú con productos

### Añadir Nuevas Funcionalidades

1. **Backend:** Crear/modificar entidades, servicios y controladores
2. **Frontend:** Crear/modificar componentes y servicios
3. **Reiniciar ambos servidores** para ver los cambios

## Documentación Adicional

- `backend/README.md` - Documentación detallada del backend
- `frontend/INTEGRACION.md` - Guía de integración frontend-backend
- `frontend/SIMPLIFICACION.md` - Historial de simplificaciones del frontend
