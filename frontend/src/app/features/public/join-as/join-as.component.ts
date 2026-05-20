// src/app/features/public/join-as/join-as.component.ts
import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { AuthService } from '../../../core/services/auth.service';
import { WorkerItem, WorkerService } from '../../../core/services/worker.service';

@Component({
  selector: 'app-join-as',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './join-as.component.html',
  styleUrls: ['./join-as.component.css'],
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
  joinAsAdmin(): void {
    this.isAdminModalOpen.set(true);
  }
  joinAsWorker(): void {
    this.isWorkerModalOpen.set(true);
  }
  joinAsUser(): void {
    this.isTableModalOpen.set(true);
  }

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
      },
    });
  }

  // --- LOGIN WORKER ---
  confirmWorkerLogin(username: string, pin: string): void {
    if (!username || !pin) {
      this.authError.set(true);
      this.errorMessage.set('Please fill both fields.');
      return;
    }

    this.authService.login(username, pin).subscribe({
      next: (res) => {
        this.closeAllModals();
        this.router.navigate(['/worker']);
      },
      error: () => {
        this.authError.set(true);
        this.errorMessage.set('Incorrect PIN.');
      },
    });
  }

  confirmTable(inputValue: string): void {
    const num = parseInt(inputValue, 10);

    if (!isNaN(num) && num > 0) {
      // Llamamos al nuevo endpoint POST del backend
      this.orderService.joinOrCreateTable(num).subscribe({
        next: () => {
          // Si entra por 'next', significa que la mesa se creó o ya existía correctamente
          this.tableError.set(false);
          this.orderService.setTableId(num);
          this.router.navigate(['/customer/customer-home']);
        },
        error: () => {
          // Error de conexión o fallo del servidor
          this.tableError.set(true);
          this.errorMessage.set('Server error. Please check your connection.');
        },
      });
    } else {
      // El usuario ha escrito letras o números negativos
      this.tableError.set(true);
      this.errorMessage.set('Please enter a valid table number.');
    }
  }
}
