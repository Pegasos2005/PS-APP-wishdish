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
- ✅ Contador de carrito (básico)
- ✅ Integración con backend vía API REST

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

## Estructura del Proyecto

```
frontend/
├── src/app/
│   ├── menu/                      # Componente principal
│   │   ├── menu.ts               # Lógica del menú
│   │   ├── menu.html             # Template
│   │   ├── menu.css              # Estilos
│   │   └── product-card/         # Componente de tarjeta
│   │       ├── product-card.ts
│   │       ├── product-card.html
│   │       └── product-card.css
│   ├── pedido/                    # Componente de pedido (WIP)
│   │   ├── pedido.ts
│   │   ├── pedido.html
│   │   └── pedido.css
│   ├── models/                    # Interfaces TypeScript
│   │   ├── menu-categoria-backend.model.ts
│   │   └── producto-backend.model.ts
│   ├── services/                  # Servicios HTTP
│   │   └── menu.service.ts       # Comunicación con API
│   ├── app.ts                     # Componente raíz
│   ├── app.config.ts              # Configuración
│   └── app.routes.ts              # Rutas
├── assets/                        # Imágenes de productos
├── index.html
├── main.ts
└── styles.css
```

## Componentes Principales

### Menu Component

**Ubicación:** `src/app/menu/`

**Responsabilidades:**
- Cargar menú desde la API
- Mostrar categorías en sidebar
- Mostrar productos agrupados por categoría
- Gestionar scroll sincronizado
- Contador de carrito

### ProductCard Component

**Ubicación:** `src/app/menu/product-card/`

**Responsabilidades:**
- Mostrar información del producto
- Emitir evento al agregar al carrito

**Input/Output:**
```typescript
@Input() product!: ProductoBackend;
@Output() addToCart = new EventEmitter<ProductoBackend>();
```

### MenuService

**Ubicación:** `src/app/services/menu.service.ts`

**Responsabilidades:**
- Comunicación con backend vía HTTP
- Endpoint: `GET http://localhost:8080/api/menu`

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

## Próximos Pasos

- [ ] Implementar carrito funcional completo
- [ ] Crear servicio de carrito persistente (LocalStorage)
- [ ] Completar componente de pedido
- [ ] Agregar manejo de errores visual
- [ ] Agregar loading states

## Documentación Adicional

- **Angular 19:** https://angular.dev
- **Integración:** Ver `INTEGRACION.md`
- **Simplificaciones:** Ver `SIMPLIFICACION.md`
- **API Backend:** Ver `../backend/README.md`
