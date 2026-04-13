// src/app/app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  // Ruta por defecto redirige a la pantalla pública
  { path: '', redirectTo: 'join-as', pathMatch: 'full' },

  // ROL: Public
  {
    path: 'join-as',
    loadComponent: () => import('./features/public/join-as/join-as.component').then(c => c.JoinAsComponent)
  },

  // ROL: Admin (Lazy Load)
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then(r => r.ADMIN_ROUTES)
  },

  // ROL: Worker (Lazy Load)
  //{
  //  path: 'worker',
  //  loadChildren: () => import('./features/worker/worker.routes').then(r => r.WORKER_ROUTES)
  //},

  // ROL: Customer (Lazy Load)
  //{
  //  path: 'customer',
  //  loadChildren: () => import('./features/customer/customer.routes').then(r => r.CUSTOMER_ROUTES)
  //},

  // Fallback para rutas no encontradas (404)
  { path: '**', redirectTo: 'join-as' } // U otra página NotFound específica
];
