// src/app/features/public/join-as/join-as.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';

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

  isTableModalOpen = signal<boolean>(false);
  tableError = signal<boolean>(false);
  errorMessage = signal<string>(''); // <--- NUEVO: Para mensajes de error personalizados

  joinAsAdmin(): void { this.router.navigate(['/admin']); }
  joinAsWorker(): void { this.router.navigate(['/worker']); }

  joinAsUser(): void {
    this.isTableModalOpen.set(true);
  }

  closeModal(): void {
    this.isTableModalOpen.set(false);
    this.tableError.set(false);
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
