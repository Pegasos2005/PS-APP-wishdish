// src/app/features/admin/admin.routes.ts
import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';

export const ADMIN_ROUTES: Routes = [
  // Redirigimos la entrada base directamente al dashboard
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  // Agrupamos TODAS las rutas del admin bajo el Guard[cite: 36]
  {
    path: '',
    canActivate: [adminGuard], // <--- EL PORTERO: Solo entra el rol ADMIN
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard.component').then(c => c.DashboardComponent)
      },
      {
        path: 'products',
        loadComponent: () => import('./product-management/product-list/product-list.component').then(c => c.ProductListComponent)
      },
      {
        path: 'products/new',
        loadComponent: () => import('./product-management/product-form/product-form.component').then(c => c.ProductFormComponent)
      },
      {
        path: 'products/edit/:id',
        loadComponent: () => import('./product-management/product-form/product-form.component').then(c => c.ProductFormComponent)
      },
      {
        path: 'ingredients/new',
        loadComponent: () => import('./product-management/ingredient-form/ingredient-form.component').then(c => c.IngredientFormComponent)
      }
    ]
  }
];
