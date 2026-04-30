import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { AuthService } from '../../../core/services/auth.service'; // <--- IMPORTANTE

@Component({
  selector: 'app-customer-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-home.component.html',
  styleUrls: ['./customer-home.component.css']
})
export class CustomerHomeComponent {
  private router = inject(Router);
  public orderService = inject(CustomerOrderService);
  private authService = inject(AuthService); // <--- INYECTAMOS

  restaurantName = signal<string>('WishDish');

  // Signals para el modal de salida segura
  isExitModalOpen = signal<boolean>(false);
  authError = signal<boolean>(false);

  startOrdering(): void {
    this.router.navigate(['/customer/customer-menu']);
  }

  watchOrders(): void {
    this.router.navigate(['/customer/customer-ticket']);
  }

  // --- LÓGICA DE SALIDA SEGURA ---
  requestExit(): void {
    this.isExitModalOpen.set(true);
  }

  cancelExit(): void {
    this.isExitModalOpen.set(false);
    this.authError.set(false);
  }

  confirmExit(username: string, pin: string): void {
    if (!username || !pin) {
      this.authError.set(true);
      return;
    }

    this.authService.login(username, pin).subscribe({
      next: (res) => {
        // Solo ADMIN o WAITER pueden desbloquear la tablet de la mesa
        if (res.role === 'ADMIN' || res.role === 'WAITER') {
          this.orderService.setTableId(null); // Liberamos la mesa en el servicio global
          this.authService.logout(); // Cerramos la sesión temporal del staff
          this.router.navigate(['/join-as']); // Volvemos al inicio
        } else {
          // Si es un KITCHEN, no le dejamos
          this.authError.set(true);
        }
      },
      error: () => {
        this.authError.set(true);
      }
    });
  }
}
