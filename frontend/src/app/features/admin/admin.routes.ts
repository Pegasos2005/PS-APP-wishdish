import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';

export const ADMIN_ROUTES: Routes = [
  // Redirigir la ruta vacía a passwd-admin
  { path: '', redirectTo: 'passwd-admin', pathMatch: 'full' },
  {
    path: 'passwd-admin',
    loadComponent: () => import('./passwd-admin/passwd-admin.component').then(c => c.PasswdAdminComponent)
  },

  {
    path: 'dashboard',
    canActivate: [adminGuard], // Si el usuario no se ha logueado como admin, no entra
    loadComponent: () => import('./dashboard/dashboard.component').then(c => c.DashboardComponent)
  }
];
