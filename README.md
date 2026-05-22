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

### Configuración
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

Además se crean los siguientes roles:
Nombre; Rol; Contraseña;
Tomás; ADMIN; admin;
Sol; KITCHEN; kitchen;
Carlos; KITCHEN; carlos;
Crisa; WAITER; waiter;

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

## Pago con Stripe (demo académica)

La integración procesa pagos reales en **modo test** de Stripe (sin webhooks). El importe se calcula en el servidor a partir de las órdenes activas de la mesa.

### Requisitos previos

- Cuenta Stripe gratuita → [dashboard.stripe.com](https://dashboard.stripe.com)
- Las claves de test se obtienen en **Developers → API keys** (modo Test activado)

### Configuración de claves

**Backend** — crea el fichero (ya está en `.gitignore`, nunca se commitea):

```
backend/src/main/resources/application-local.properties
```

```properties
stripe.secret.key=sk_test_TU_CLAVE_SECRETA
stripe.currency=eur
```

**Frontend** — edita ambos ficheros de entorno:

- `frontend/src/environments/environment.ts`
- `frontend/src/environments/environment.development.ts`

```ts
export const environment = {
  production: false,
  apiUrl: 'http://' + window.location.hostname + ':8080/api/',
  stripePublicKey: 'pk_test_TU_CLAVE_PUBLICA'
};
```

### Arrancar el backend con Stripe activo

El profile `local` es obligatorio para cargar la `sk_test_`:

**Desde IntelliJ:** Edit Run Configuration → Active profiles: `local` → Run

**Desde terminal** (requiere JDK instalado):
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### MySQL sin instalación local (Podman)

Fedora incluye Podman por defecto. No requiere instalar MySQL en el sistema:

```bash
# Arrancar (primera vez)
podman run -d --name wishdish-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=wishdish \
  -p 3306:3306 \
  docker.io/mysql:8.0

# Arranques posteriores
podman start wishdish-mysql

# Parar
podman stop wishdish-mysql
```

El backend se conecta automáticamente a `localhost:3306` con usuario `root/root`.

Ejecutar queries desde el contenedor (sin instalar cliente MySQL):
```bash
podman exec -it wishdish-mysql mysql -uroot -proot wishdish -e "SELECT * FROM payment_transactions;"
```

### Flujo de pago

1. Cliente selecciona mesa → hace comanda → va al ticket
2. Pulsa **"Pay now"** → navega a la página de pago Stripe
3. El backend calcula el total de las órdenes activas (con IGIC incluido en `unitPrice`)
4. El cliente introduce los datos de tarjeta en el Payment Element de Stripe
5. Stripe procesa el cobro → el frontend llama a `/api/payments/confirm`
6. El backend verifica el pago contra la API de Stripe (server-to-server) y cierra la mesa

### Tarjetas de test

| Tarjeta | Resultado |
|---|---|
| `4242 4242 4242 4242` | Pago aprobado ✅ |
| `4000 0000 0000 0002` | Pago rechazado ❌ |

CVC y fecha de caducidad: cualquier valor válido (ej. `123` / `12/30`).

### Endpoints de pago

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/payments/create-intent` | Body: `{"tableNumber": 1}`. Crea PaymentIntent en Stripe y devuelve `clientSecret`. |
| POST | `/api/payments/confirm` | Body: `{"paymentIntentId": "pi_..."}`. Verifica con Stripe y cierra la mesa. Idempotente. |

### Verificar resultados en BD

```bash
# Estado de las transacciones
podman exec -it wishdish-mysql mysql -uroot -proot wishdish -e "
SELECT stripe_payment_intent_id, table_number, amount, status
FROM payment_transactions ORDER BY created_at DESC LIMIT 5;"

# Confirmar que las órdenes de la mesa pasaron a 'paid'
podman exec -it wishdish-mysql mysql -uroot -proot wishdish -e "
SELECT o.id, o.status, dt.table_number FROM orders o
JOIN dining_tables dt ON dt.id = o.table_id
ORDER BY o.id DESC LIMIT 10;"
```

También puedes ver los pagos en [dashboard.stripe.com/test/payments](https://dashboard.stripe.com/test/payments).

### Limitaciones conocidas (académicas)

- **Sin webhooks**: si el navegador se cierra entre el éxito de Stripe y la llamada a `/confirm`, el cobro queda en Stripe pero la mesa no se cierra automáticamente. Recovery: reintentar `/confirm` con el `paymentIntentId`.
- **Métodos de redirección** (iDEAL, Sofort…): requieren `return_url` para el retorno. En la demo usar solo tarjeta.
- **Sin Spring Security**: los endpoints `/api/payments/*` no requieren autenticación. Aceptable en entorno académico local.

## Documentación Adicional
