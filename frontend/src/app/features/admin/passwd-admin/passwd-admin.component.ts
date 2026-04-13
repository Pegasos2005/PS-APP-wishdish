// src/app/features/admin/passwd-admin.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-passwd-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './passwd-admin.component.html',
  styleUrls: ['./passwd-admin.component.css']
})
export class PasswdAdminComponent {
  private router = inject(Router);
  private authService = inject(AuthService); // <--- Inyectamos el servicio

  passwordError = signal<boolean>(false);

  verifyPassword(passwordInput: string): void {
    const masterPassword = 'admin';

    if (passwordInput === masterPassword) {
      this.passwordError.set(false);

      // 1. Avisamos al servicio de que el login es exitoso
      this.authService.loginAdmin();

      // 2. Navegamos al dashboard
      this.router.navigate(['/admin/dashboard']);
    } else {
      this.passwordError.set(true);
      setTimeout(() => this.passwordError.set(false), 3000);
    }
  }

  /**
   * Vuelve a la pantalla principal de "Join As"
   */
  goBack(): void {
    this.router.navigate(['/join-as']);
  }
}
