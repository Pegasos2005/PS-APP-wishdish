// src/app/features/worker/worker.routes.ts
import { Routes } from '@angular/router';

export const WORKER_ROUTES: Routes = [
  { path: '', redirectTo: 'worker-view', pathMatch: 'full' },
  {
    path: '',
    loadComponent: () => import('./worker-view/worker-view.component').then(c => c.WorkerViewComponent)
  }
];
