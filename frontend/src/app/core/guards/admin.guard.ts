// src/app/core/guards/admin.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const role = authService.currentUserRole()?.toUpperCase();

  if (role === 'ADMIN') {
    return true;
  }

  router.navigate(['/join-as']);
  return false;
};
