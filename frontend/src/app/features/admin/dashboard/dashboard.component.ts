import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

// Interfaz para tipar nuestros botones del menú
interface AdminMenuItem {
  id: number;
  title: string;
  icon: string;
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
    { id: 1, title: 'Manage Catalog', icon: '🍔', action: 'manage-catalog' },
    { id: 2, title: 'Change User Table', icon: '🔄', action: 'change-table' },
    { id: 3, title: 'Edit Comand', icon: '📝', action: 'edit-comand' },
    { id: 4, title: 'Manage Devices', icon: '📱', action: 'manage-devices' },
    { id: 5, title: 'Close Cash Register', icon: '💶', action: 'close-cash' },
    { id: 6, title: 'Close Table', icon: '🔒', action: 'close-table' },
    { id: 7, title: 'Staff Management', icon: '👥', action: 'staff' },
    { id: 8, title: 'Inventory', icon: '📦', action: 'inventory' },
    { id: 9, title: 'Settings', icon: '⚙️', action: 'settings' },
    // Página 2
    { id: 10, title: 'Stats & Analytics', icon: '📊', action: 'stats' },
    { id: 11, title: 'Providers', icon: '🚚', action: 'providers' },
    { id: 12, title: 'Discount Codes', icon: '🎟️', action: 'discounts' },
    { id: 13, title: 'QR Generator', icon: '🔳', action: 'qr-gen' },
    { id: 14, title: 'Daily Reports', icon: '📅', action: 'reports' },
    { id: 15, title: 'Printer Config', icon: '🖨️', action: 'printer' },
    { id: 16, title: 'Tax Settings', icon: '🏛️', action: 'taxes' },
    { id: 17, title: 'Backup Data', icon: '💾', action: 'backup' },
    { id: 18, title: 'System Logs', icon: '📋', action: 'logs' }
  ];

  // Signal Computado
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

  // Lógica de navegación centralizada
  handleItemClick(action: string) {
    switch (action) {
      case 'manage-catalog':
        this.router.navigate(['/admin/crud-menu']);
        break;
      case 'change-table':
        this.router.navigate(['/admin/table-management']);
        break;
      case 'staff':
        this.router.navigate(['/admin/staff-management']);
        break;
      case 'edit-comand':
        this.router.navigate(['/admin/edit-comand-select']);
        break;
      default:
        console.log('Acción no implementada todavía:', action);
        break;
    }
  }

  // Logout
  logout() {
    this.authService.logout();
    this.router.navigate(['/join-as']);
  }
}
