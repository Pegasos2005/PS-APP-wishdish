import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';

export const ADMIN_ROUTES: Routes = [
  { path: '', redirectTo: 'passwd-admin', pathMatch: 'full' },
  {
    path: 'passwd-admin',
    loadComponent: () => import('./passwd-admin/passwd-admin.component').then(c => c.PasswdAdminComponent)
  },
  {
    path: '',
    canActivate: [adminGuard],
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
