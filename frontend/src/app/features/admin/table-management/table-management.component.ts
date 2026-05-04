import { Component, OnInit, OnDestroy, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, interval, switchMap } from 'rxjs';
import { CustomerOrderService, TableOccupancy } from '../../../core/services/customer-order.service';

@Component({
  selector: 'app-table-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './table-management.component.html',
  styleUrls: ['./table-management.component.css']
})
export class TableManagementComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private orderService = inject(CustomerOrderService);

  occupancy = signal<TableOccupancy[]>([]);
  fromTable = signal<number | null>(null);
  toTable = signal<number | null>(null);
  feedback = signal<{ ok: boolean; message: string } | null>(null);
  isSubmitting = signal<boolean>(false);

  occupiedTables = computed(() => this.occupancy().filter(t => t.occupied));
  freeTables = computed(() => this.occupancy().filter(t => !t.occupied));

  private refreshSub?: Subscription;

  ngOnInit(): void {
    this.loadOccupancy();
    // Refresco periódico para que el grid refleje cambios de mesas en tiempo casi real
    this.refreshSub = interval(5000)
      .pipe(switchMap(() => this.orderService.getOccupancy()))
      .subscribe({
        next: (data) => this.occupancy.set(data),
        error: (err) => console.error('Error refrescando ocupación:', err)
      });
  }

  ngOnDestroy(): void {
    this.refreshSub?.unsubscribe();
  }

  loadOccupancy(): void {
    this.orderService.getOccupancy().subscribe({
      next: (data) => this.occupancy.set(data),
      error: (err) => console.error('Error cargando ocupación:', err)
    });
  }

  reassign(): void {
    const from = this.fromTable();
    const to = this.toTable();
    if (from === null || to === null) {
      this.feedback.set({ ok: false, message: 'Select source and target tables.' });
      return;
    }
    if (from === to) {
      this.feedback.set({ ok: false, message: 'Source and target must differ.' });
      return;
    }

    this.isSubmitting.set(true);
    this.orderService.reassignTable(from, to).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.feedback.set({ ok: true, message: `Table ${from} reassigned to ${to}.` });
        this.fromTable.set(null);
        this.toTable.set(null);
        this.loadOccupancy();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const msg = err?.error?.message || err?.message || 'Reassignment failed.';
        this.feedback.set({ ok: false, message: msg });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}
