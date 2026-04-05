# Frontend - WishDish

Aplicación web para visualización del menú de WishDish, construida con Angular 19.

## Tecnologías

- Angular 19
- TypeScript
- Node.js 18+
- Angular Signals (gestión de estado)

## Requisitos

- Node.js 18 o superior
- npm 9 o superior

### Verificar versiones

```bash
node --version   # >= v18
npm --version    # >= v9
```

## Instalación

```bash
cd frontend
npm install
```

## Ejecución

```bash
npm start
```

O alternativamente:

```bash
ng serve
```

La aplicación estará disponible en: **http://localhost:4200**

### Con puerto personalizado

```bash
ng serve --port 4300
```

### Abrir automáticamente en navegador

```bash
ng serve --open
```

## Estado Actual

### Funcionalidades Implementadas

- ✅ Vista de menú con productos organizados por categorías
- ✅ Tarjetas de producto con imagen, nombre, descripción y precio
- ✅ Sidebar de categorías
- ✅ Scroll sincronizado entre categorías y productos
- ✅ Contador de carrito y creación de comandas
- ✅ Vista de camarero con listado de comandas activas
- ✅ Navegación entre vista cliente y vista camarero
- ✅ Sistema de rutas con Angular Router
- ✅ Polling automático cada 3 segundos para actualizar comandas
- ✅ Integración completa con API REST

### Integración con Backend

El frontend se comunica con el backend a través del servicio `MenuService` que consume la API REST.

**Configuración:** Ver `src/app/app.config.ts`

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient()  // ← Habilitado para comunicación con backend
  ]
};
```

**Servicio:** Ver `src/app/services/menu.service.ts`

El servicio carga los datos desde `http://localhost:8080/api/menu`

**Importante:** El backend debe estar corriendo en el puerto 8080 para que funcione correctamente.

## Arquitectura y Rutas

### Sistema de Rutas

La aplicación utiliza **Angular Router** para gestionar la navegación entre vistas:

**Archivo:** `src/app/app.routes.ts`

```typescript
export const routes: Routes = [
  { path: '', component: Menu },        // Vista cliente (por defecto)
  { path: 'pedido', component: Pedido } // Vista camarero
];
```

**Componente raíz:** El `App` component (`src/app/app.ts`) usa `<router-outlet>` para renderizar dinámicamente los componentes según la ruta activa.

### Flujo de Navegación

```
┌─────────────────────────────────────────┐
│  http://localhost:4200/                 │
│  ↓                                       │
│  Menu Component (Vista Cliente)         │
│  - Mostrar productos                    │
│  - Agregar al carrito                   │
│  - Botón "Send Order"                   │
│  - Botón "Vista Camarero" → /pedido     │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│  http://localhost:4200/pedido           │
│  ↓                                       │
│  Pedido Component (Vista Camarero)      │
│  - Listar comandas activas              │
│  - Actualización automática (polling)   │
│  - Botón "Vista Cliente" → /            │
└─────────────────────────────────────────┘
```

## Estructura del Proyecto

```
frontend/
├── src/app/
│   ├── menu/                      # Vista cliente - Menú
│   │   ├── menu.ts               # Lógica: carrito, navegación
│   │   ├── menu.html             # Template con categorías/productos
│   │   ├── menu.css              # Estilos
│   │   └── product-card/         # Componente de tarjeta
│   │       ├── product-card.ts
│   │       ├── product-card.html
│   │       └── product-card.css
│   ├── pedido/                    # Vista camarero - Comandas
│   │   ├── pedido.ts             # Lógica: polling, navegación
│   │   ├── pedido.html           # Template con grid de comandas
│   │   └── pedido.css            # Estilos de tarjetas
│   ├── models/                    # Interfaces TypeScript
│   │   ├── menu-categoria.model.ts
│   │   ├── producto.model.ts
│   │   └── comanda.model.ts      # DTOs de comandas
│   ├── services/                  # Servicios HTTP
│   │   ├── menu.service.ts       # API de menú
│   │   └── comanda.service.ts    # API de comandas
│   ├── app.ts                     # Componente raíz con <router-outlet>
│   ├── app.html                   # Template raíz
│   ├── app.config.ts              # Configuración (HttpClient, Router)
│   └── app.routes.ts              # Definición de rutas
├── assets/                        # Imágenes de productos
├── index.html
├── main.ts
└── styles.css
```

## Componentes Principales

### App Component (Raíz)

**Ubicación:** `src/app/app.ts`

**Responsabilidades:**
- Renderizar `<router-outlet>` para navegación
- Punto de entrada de la aplicación

**Template:**
```html
<router-outlet></router-outlet>
```

### Menu Component (Vista Cliente)

**Ubicación:** `src/app/menu/`
**Ruta:** `/` (raíz)

**Responsabilidades:**
- Cargar menú desde la API
- Mostrar categorías en sidebar
- Mostrar productos agrupados por categoría
- Gestionar scroll sincronizado
- Gestionar carrito de compras
- Crear comandas vía API
- Navegación a vista camarero

**Funcionalidades:**
```typescript
onAddToCart(product)      // Agregar producto al carrito
sendOrder()               // Crear comanda en el backend
navigateToPedido()        // Ir a vista camarero
```

### Pedido Component (Vista Camarero)

**Ubicación:** `src/app/pedido/`
**Ruta:** `/pedido`

**Responsabilidades:**
- Listar comandas activas (estados: en_cocina, servida)
- Actualización automática cada 3 segundos (polling)
- Mostrar items de cada comanda
- Navegación a vista cliente

**Características:**
```typescript
cargarComandasActivas()   // Carga inicial desde API
iniciarPolling()          // Actualización automática
navigateToMenu()          // Volver a vista cliente
```

**Datos mostrados:**
- Número de mesa
- Estado de comanda (En Cocina / Servida)
- Lista de items con cantidad y nombre
- Estado de cada item

### ProductCard Component

**Ubicación:** `src/app/menu/product-card/`

**Responsabilidades:**
- Mostrar información del producto
- Emitir evento al agregar al carrito

**Input/Output:**
```typescript
@Input() product!: Producto;
@Output() addToCart = new EventEmitter<Producto>();
```

## Servicios

### MenuService

**Ubicación:** `src/app/services/menu.service.ts`

**Endpoints:**
- `GET http://localhost:8080/api/menu` - Obtener menú completo

### ComandaService

**Ubicación:** `src/app/services/comanda.service.ts`

**Endpoints:**
- `GET http://localhost:8080/api/comandas/activas` - Listar comandas activas
- `POST http://localhost:8080/api/comandas` - Crear nueva comanda
- `PUT http://localhost:8080/api/comandas/items/{id}/avanzar` - Avanzar estado de item

## Scripts Disponibles

```bash
npm start          # Servidor de desarrollo
npm run build      # Build de producción
npm test           # Ejecutar tests
```

## Build de Producción

```bash
ng build --configuration=production
```

Los archivos compilados estarán en: `dist/frontend/browser/`

**Características:**
- Minificación de código
- Optimización de bundle
- Listo para deploy

## Desarrollo

### Agregar nuevo componente

```bash
ng generate component nombre-componente
```

### Agregar nuevo servicio

```bash
ng generate service services/nombre-servicio
```

### Agregar nueva interfaz

```bash
ng generate interface models/nombre-modelo
```

## Solución de Problemas

### Error: Backend no responde

**Verificar:**
1. Backend está corriendo en `http://localhost:8080`
2. MySQL está corriendo y tiene datos
3. No hay errores CORS en la consola

**Test rápido:**
```bash
curl http://localhost:8080/api/menu
```

### Error: Puerto 4200 ocupado

```bash
# Opción 1: Usar otro puerto
ng serve --port 4300

# Opción 2: Matar proceso (Windows)
netstat -ano | findstr :4200
taskkill /PID <PID> /F
```

### Error: Cannot find module

```bash
rm -rf node_modules package-lock.json
npm install
```

### Productos no se muestran

**Verificar en consola del navegador (F12):**
1. ¿Hay errores HTTP?
2. ¿La respuesta de `/api/menu` tiene datos?
3. ¿El backend está corriendo?

## CORS

El backend ya está configurado para aceptar peticiones desde `http://localhost:4200`.

Si cambias el puerto del frontend, actualiza la configuración CORS en el backend (`WebConfig.java`).

## Sintaxis de Angular 19

El proyecto utiliza la nueva sintaxis de control flow de Angular 19:

**Condicionales:**
```html
@if (condition) {
  <div>Contenido</div>
}
```

**Bucles:**
```html
@for (item of items; track item.id) {
  <div>{{ item.name }}</div>
}
```

**Nota:** La sintaxis antigua (`*ngIf`, `*ngFor`) no se utiliza en este proyecto.

## Próximos Pasos

- [ ] Implementar selector de mesa en vista cliente
- [ ] Agregar funcionalidad de avance de estado de items
- [ ] Crear servicio de carrito persistente (LocalStorage)
- [ ] Agregar manejo de errores visual mejorado
- [ ] Agregar loading states
- [ ] Agregar filtros en vista camarero

## Documentación Adicional

- **Angular 19:** https://angular.dev
- **Integración:** Ver `INTEGRACION.md`
- **Simplificaciones:** Ver `SIMPLIFICACION.md`
- **API Backend:** Ver `../backend/README.md`
