// src/app/core/guards/admin.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Le preguntamos al servicio si el admin está autenticado
  if (authService.isAdminAuthenticated()) {
    return true; // Le dejamos pasar al dashboard
  } else {
    // No está autenticado. Lo pateamos de vuelta a la pantalla de contraseña
    router.navigate(['/admin/passwd-admin']);
    return false; // Bloqueamos el acceso a la ruta actual
  }
};
