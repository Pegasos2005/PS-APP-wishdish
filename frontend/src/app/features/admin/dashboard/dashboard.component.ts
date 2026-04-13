import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

// Interfaz para tipar nuestros botones del menú
interface AdminMenuItem {
  id: number;
  title: string;
  icon: string; // Usaremos emojis como iconos temporales para que se vea vistoso sin instalar librerías
  action: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  // Control de paginación
  currentPage = signal<number>(0);
  itemsPerPage = 9;

  // Los 18 botones (9 reales + 9 ejemplos para la página 2)
  menuItems: AdminMenuItem[] = [
    // Página 1
    { id: 1, title: 'Edit Product', icon: '🍔', action: 'edit-product' },
    { id: 2, title: 'Change User Table', icon: '🔄', action: 'change-table' },
    { id: 3, title: 'Edit Comand', icon: '📝', action: 'edit-comand' },
    { id: 4, title: 'Manage Devices', icon: '📱', action: 'manage-devices' },
    { id: 5, title: 'Close Cash Register', icon: '💶', action: 'close-cash' },
    { id: 6, title: 'Close Table', icon: '🔒', action: 'close-table' },
    { id: 7, title: 'Stats & Analytics', icon: '📊', action: 'stats' },
    { id: 8, title: 'Inventory', icon: '📦', action: 'inventory' },
    { id: 9, title: 'Settings', icon: '⚙️', action: 'settings' },
    // Página 2 (Simulada para que funcione la flecha)
    { id: 10, title: 'Staff Management', icon: '👥', action: 'staff' },
    { id: 11, title: 'Providers', icon: '🚚', action: 'providers' },
    { id: 12, title: 'Discount Codes', icon: '🎟️', action: 'discounts' },
    { id: 13, title: 'QR Generator', icon: '🔳', action: 'qr-gen' },
    { id: 14, title: 'Daily Reports', icon: '📅', action: 'reports' },
    { id: 15, title: 'Printer Config', icon: '🖨️', action: 'printer' },
    { id: 16, title: 'Tax Settings', icon: '🏛️', action: 'taxes' },
    { id: 17, title: 'Backup Data', icon: '💾', action: 'backup' },
    { id: 18, title: 'System Logs', icon: '📋', action: 'logs' }
  ];

  // Signal Computado: Calcula automáticamente qué botones mostrar según la página
  visibleItems = computed(() => {
    const start = this.currentPage() * this.itemsPerPage;
    return this.menuItems.slice(start, start + this.itemsPerPage);
  });

  // Funciones de navegación del menú
  nextPage() {
    if ((this.currentPage() + 1) * this.itemsPerPage < this.menuItems.length) {
      this.currentPage.update(p => p + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
    }
  }

  handleItemClick(action: string) {
    console.log('Navegando a la acción:', action);
    // Aquí pondrías el switch o la navegación: this.router.navigate([`/admin/${action}`]);
  }

  // ¡MUY IMPORTANTE! Al salir, debemos borrar la sesión del guard
  logout() {
    this.authService.logoutAdmin(); // Borra el estado de autenticación
    this.router.navigate(['/join-as']); // Vuelve a la pantalla principal
  }
}
