import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { WorkerService, WorkerItem } from '../../../core/services/worker.service';
import { CommonModule } from '@angular/common';

import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';


@Component({
  selector: 'app-staff-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './staff-management.component.html',
  styleUrl: './staff-management.component.css',
})
export class StaffManagementComponent implements OnInit{
  private workerService = inject(WorkerService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  // Lista de trabajadores
  workers = signal<WorkerItem[]>([]);

  showAddModal = signal<boolean>(false);
  workerForm: FormGroup;

  constructor() {
    // Inicializamos el formulario limpio
    this.workerForm = this.fb.group({
      name: ['', Validators.required],
      role: ['CAMARERO', Validators.required], // Valor por defecto
      pin: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(6)]]
    });
  }

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
    this.workerForm.reset({ role: 'CAMARERO' }); // Limpiamos
    this.showAddModal.set(true); // Abrimos modal
  }

  closeModal() {
    this.showAddModal.set(false);
  }

  saveWorker() {
    if (this.workerForm.invalid) {
      alert('Please fill all required fields correctly.');
      return;
    }

    const newWorkerData: WorkerItem = this.workerForm.value;

    this.workerService.createWorker(newWorkerData).subscribe({
      next: (createdWorker) => {
        // Añadimos el nuevo trabajador a la lista
        this.workers.update(list => [...list, createdWorker]);
        this.closeModal();
        console.log('Trabajador creado con éxito');
      },
      error: (err) => {
        console.error('Error al crear trabajador:', err);
        alert('Could not save the worker. Check connection.');
      }
    });
  }

  editWorker(worker: WorkerItem) { console.log('Editar', worker); }
  deleteWorker(worker: WorkerItem) { console.log('Borrar', worker); }
}
