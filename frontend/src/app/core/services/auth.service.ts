// src/app/core/services/auth.service.ts
import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Inicializamos el Signal leyendo el sessionStorage por si el usuario recarga la página
  isAdminAuthenticated = signal<boolean>(this.checkSession());

  private checkSession(): boolean {
    return sessionStorage.getItem('adminAuth') === 'true';
  }

  // Se llama cuando mete la clave correcta
  loginAdmin(): void {
    sessionStorage.setItem('adminAuth', 'true');
    this.isAdminAuthenticated.set(true);
  }

  // Se llama cuando el admin le da al botón de "Cerrar Sesión"
  logoutAdmin(): void {
    sessionStorage.removeItem('adminAuth');
    this.isAdminAuthenticated.set(false);
  }
}
