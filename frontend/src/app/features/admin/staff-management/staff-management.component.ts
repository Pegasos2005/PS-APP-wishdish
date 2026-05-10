import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { WorkerService, WorkerItem } from '../../../core/services/worker.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-staff-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './staff-management.component.html',
  styleUrl: './staff-management.component.css',
})
export class StaffManagementComponent implements OnInit{
  private workerService = inject(WorkerService);
  private router = inject(Router);

  // Lista de trabajadores
  workers = signal<WorkerItem[]>([]);

  ngOnInit() {
    this.loadWorkers();
  }
  loadWorkers() {
    this.workerService.getWorkers().subscribe({
      next: (data) => this.workers.set(data),
      error: (err) => console.error('Error cargando la lista de trabajadores:', err)
    });
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }

  addNewWorker() {
    console.log('Botón añadir pulsado');
  }

  editWorker(worker: WorkerItem) {
    console.log('Botón editar pulsado', worker);
  }

  deleteWorker(worker: WorkerItem) {
    console.log('Botón borrar pulsado', worker);
  }
}
