import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { ComandaResponseDTO } from '../../../core/models/comanda.model';

@Component({
  selector: 'app-edit-comand-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './edit-comand-select.component.html',
  styleUrls: ['./edit-comand-select.component.css']
})
export class EditComandSelectComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  // Lista reactiva de comandas activas
  activeOrders = signal<ComandaResponseDTO[]>([]);
  loading = signal<boolean>(true);

  ngOnInit() {
    this.loadActiveOrders();
  }

  loadActiveOrders() {
    this.loading.set(true);
    this.http.get<ComandaResponseDTO[]>(`${environment.apiUrl}orders/active`).subscribe({
      next: (orders) => {
        this.activeOrders.set(orders);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar comandas activas:', err);
        this.loading.set(false);
      }
    });
  }

  goToEdit(orderId: number) {
    this.router.navigate(['/admin/edit-comand', orderId]);
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }
}
