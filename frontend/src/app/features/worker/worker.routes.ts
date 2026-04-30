// src/app/features/worker/worker.routes.ts
import { Routes } from '@angular/router';
import { kitchenGuard } from '../../core/guards/kitchen.guard';

export const WORKER_ROUTES: Routes = [
  { path: '', redirectTo: 'worker-view', pathMatch: 'full' },
  {
    path: 'worker-view',
    canActivate: [kitchenGuard], // <--- EL PORTERO: Solo entran KITCHEN y ADMIN
    loadComponent: () => import('./worker-view/worker-view.component').then(c => c.WorkerViewComponent)
  }
];
