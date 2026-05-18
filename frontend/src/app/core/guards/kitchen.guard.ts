// src/app/core/guards/kitchen.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const kitchenGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const role = authService.currentUserRole();

  if (role === 'ADMIN' || role === 'KITCHEN') {
    return true;
  }

  router.navigate(['/join-as']);
  return false;
};
