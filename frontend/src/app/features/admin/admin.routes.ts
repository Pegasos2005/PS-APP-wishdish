// src/app/features/admin/admin.routes.ts
import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';

export const ADMIN_ROUTES: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

  {
    path: '',
    canActivate: [adminGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard.component').then(c => c.DashboardComponent)
      },
      // Nueva página intermedia (Hub de gestión)
      {
        path: 'crud-menu',
        loadComponent: () => import('./crud-menu/crud-menu.component').then(c => c.CrudMenuComponent)
      },

      // --- TABLE MANAGEMENT (reasignación de mesa) ---
      {
        path: 'table-management',
        loadComponent: () => import('./table-management/table-management.component').then(c => c.TableManagementComponent)
      },

      // --- PRODUCT MANAGEMENT ---
      {
        path: 'product-management/product-list',
        loadComponent: () => import('./product-management/product-list/product-list.component').then(c => c.ProductListComponent)
      },
      {
        path: 'product-management/product-form',
        loadComponent: () => import('./product-management/product-form/product-form.component').then(c => c.ProductFormComponent)
      },
      {
        path: 'product-management/product-form/:id',
        loadComponent: () => import('./product-management/product-form/product-form.component').then(c => c.ProductFormComponent)
      },

      // --- INGREDIENT MANAGEMENT ---
      {
        path: 'ingredient-management/ingredient-list',
        loadComponent: () => import('./ingredient-management/ingredient-list/ingredient-list.component').then(c => c.IngredientListComponent)
      },
      {
        path: 'ingredient-management/ingredient-form',
        loadComponent: () => import('./ingredient-management/ingredient-form/ingredient-form.component').then(c => c.IngredientFormComponent)
      },
      {
        path: 'ingredient-management/ingredient-form/:id',
        loadComponent: () => import('./ingredient-management/ingredient-form/ingredient-form.component').then(c => c.IngredientFormComponent)
      },
      {
         path: 'product-management/ingredient-picker', // Definimos la ruta
         loadComponent: () => import('./product-management/ingredient-picker/ingredient-picker.component').then(c => c.IngredientPickerComponent)
       },
       {
         path: 'product-management/ingredient-picker/:id',
         loadComponent: () => import('./product-management/ingredient-picker/ingredient-picker.component').then(c => c.IngredientPickerComponent)
       }

      // --- CATEGORY MANAGEMENT ---
      ,
      {
        path: 'category-management',
        loadComponent: () => import('./category-management/category-list.component').then(c => c.CategoryManagementComponent)
      },
      {
        path: 'category-management/new',
        loadComponent: () => import('./category-management/category-form/category-form.component').then(c => c.CategoryFormComponent)
      },
      {
        path: 'category-management/edit/:id',
        loadComponent: () => import('./category-management/category-form/category-form.component').then(c => c.CategoryFormComponent)
      },
      {
        path: 'close-cash',
        loadComponent: () => import('./close-cash/close-cash.component').then(c => c.CloseCashComponent)
      },
      {
        path: 'close-table',
        loadComponent: () => import('./close-table/close-table.component').then(c => c.CloseTableComponent)
      }
    ]
  }
];
