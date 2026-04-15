// src/app/features/customer/customer.routes.ts
import { Routes } from '@angular/router';

export const CUSTOMER_ROUTES: Routes = [
  { path: '', redirectTo: 'customer-home', pathMatch: 'full' },
  {
    path: 'customer-home',
    loadComponent: () => import('./customer-home/customer-home.component').then(c => c.CustomerHomeComponent)
  },
  {
    path: 'customer-menu',
    loadComponent: () => import('./customer-menu/customer-menu.component').then(c => c.CustomerMenuComponent)
  },
  {
    path: 'show-order',
    loadComponent: () => import('./show-order/show-order.component').then(c => c.ShowOrderComponent)
  },
  {
    path: 'customer-ticket',
    loadComponent: () => import('./customer-ticket/customer-ticket.component').then(c => c.CustomerTicketComponent)
  }
];
