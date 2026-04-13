// src/app/features/customer/customer.routes.ts
import { Routes } from '@angular/router';

export const CUSTOMER_ROUTES: Routes = [
  { path: '', redirectTo: 'customer-home', pathMatch: 'full' },
  {
    path: 'customer-home',
    loadComponent: () => import('./customer-home/customer-home.component').then(c => c.CustomerHomeComponent)
  },
/*
  {
    path: 'home',
    loadComponent: () => import('./home/home.component').then(c => c.HomeComponent)
  },
  {
    path: 'menu',
    loadComponent: () => import('./menu/menu.component').then(c => c.MenuComponent)
  },
  {
    // Fíjate en el /:id, vital para recuperar el ProductDTO correcto
    path: 'product-details/:id',
    loadComponent: () => import('./product-details/product-details.component').then(c => c.ProductDetailsComponent)
  },
  {
    path: 'order-list',
    loadComponent: () => import('./order-list/order-list.component').then(c => c.OrderListComponent)
  },
  {
    path: 'ticket-view',
    loadComponent: () => import('./ticket-view/ticket-view.component').then(c => c.TicketViewComponent)
  }*/
];
