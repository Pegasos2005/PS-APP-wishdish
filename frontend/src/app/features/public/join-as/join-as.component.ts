// src/app/features/public/join-as/join-as.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-join-as',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './join-as.component.html',
  styleUrls: ['./join-as.component.css']
})
export class JoinAsComponent {
  private router = inject(Router);
  private orderService = inject(CustomerOrderService);
  private authService = inject(AuthService);

  // Estados de los modales
  isTableModalOpen = signal<boolean>(false);
  isAdminModalOpen = signal<boolean>(false); // Modal de Admin (solo password)
  isWorkerModalOpen = signal<boolean>(false); // Modal de Worker (User + password)

  tableError = signal<boolean>(false);
  authError = signal<boolean>(false);
  errorMessage = signal<string>('');

  // --- BOTONES PRINCIPALES ---
  joinAsAdmin(): void { this.isAdminModalOpen.set(true); }
  joinAsWorker(): void { this.isWorkerModalOpen.set(true); }
  joinAsUser(): void { this.isTableModalOpen.set(true); }

  closeAllModals(): void {
    this.isTableModalOpen.set(false);
    this.isAdminModalOpen.set(false);
    this.isWorkerModalOpen.set(false);
    this.tableError.set(false);
    this.authError.set(false);
  }

  // --- LOGIN ADMIN ---
  confirmAdminLogin(pin: string): void {
    this.authService.login(null, pin).subscribe({
      next: () => {
        this.closeAllModals();
        this.router.navigate(['/admin']);
      },
      error: () => {
        this.authError.set(true);
        this.errorMessage.set('Incorrect password.');
      }
    });
  }

  // --- LOGIN WORKER ---
  confirmWorkerLogin(username: string, pin: string): void {
    if(!username || !pin) {
      this.authError.set(true);
      this.errorMessage.set('Please fill both fields.');
      return;
    }

    this.authService.login(username, pin).subscribe({
      next: (res) => {
        this.closeAllModals();
        // Si el login es correcto, el backend ya habrá comprobado que existe[cite: 43]
        this.router.navigate(['/worker']);
      },
      error: () => {
        this.authError.set(true);
        this.errorMessage.set('Incorrect username or password.');
      }
    });
  }

  confirmTable(inputValue: string): void {
    const num = parseInt(inputValue, 10);

    if (!isNaN(num) && num > 0) {
      // 1. Preguntamos a Spring Boot si la mesa existe
      this.orderService.checkTableExists(num).subscribe({
        next: (exists: boolean) => {
          if (exists) {
            // 2. ¡La mesa existe! Le dejamos pasar
            this.tableError.set(false);
            this.orderService.setTableId(num);
            this.router.navigate(['/customer/customer-home']);
          } else {
            // 3. La mesa NO existe en la base de datos
            this.tableError.set(true);
            this.errorMessage.set("The selected table doesn't exist.");
          }
        },
        error: () => {
          // Error de conexión
          this.tableError.set(true);
          this.errorMessage.set("Server error. Please check your connection.");
        }
      });
    } else {
      // 4. El usuario ha escrito letras o números negativos
      this.tableError.set(true);
      this.errorMessage.set("Please enter a valid table number.");
    }
  }
}
