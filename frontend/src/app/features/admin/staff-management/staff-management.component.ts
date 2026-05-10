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

  editingWorkerId = signal<number | null>(null);
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
    this.editingWorkerId.set(null); // Modo "Crear"
    this.workerForm.reset({ role: 'CAMARERO' }); // Limpiamos
    this.showAddModal.set(true); // Abrimos modal
  }

  closeModal() {
    this.showAddModal.set(false);
    this.editingWorkerId.set(null); // Limpiamos el estado
  }

  saveWorker() {
    if (this.workerForm.invalid) {
      alert('Please fill all required fields correctly.');
      return;
    }

    const workerData: WorkerItem = this.workerForm.value;
    const currentEditId = this.editingWorkerId();

    if (currentEditId !== null) {
      this.workerService.updateWorker(currentEditId, workerData).subscribe({
        next: (updatedWorker) => {
          // Sustituimos al trabajador antiguo por el actualizado en la lista visual
          this.workers.update(list =>
            list.map(w => w.id === currentEditId ? updatedWorker : w)
          );
          this.closeModal();
          console.log('Trabajador actualizado con éxito');
        },
        error: (err) => {
          console.error('Error al actualizar trabajador:', err);
          alert('Could not update the worker. Check connection.');
        }
      });
    } else {
        this.workerService.createWorker(workerData).subscribe({
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
  }

  editWorker(worker: WorkerItem) {
    if (!worker.id) return;
    this.editingWorkerId.set(worker.id); // Activamos modo "Editar"

    // Rellenamos automáticamente los inputs con los datos del trabajador
    this.workerForm.patchValue({
      name: worker.name,
      role: worker.role,
      pin: worker.pin || ''
    });

    this.showAddModal.set(true); // Abrimos el mismo modal
  }

  deleteWorker(worker: WorkerItem) {
    // 1. Pedimos confirmación al usuario
    const confirmDelete = confirm(`Are you sure you want to delete ${worker.name}? This action cannot be undone.`);

    if (confirmDelete && worker.id) {
      this.workerService.deleteWorker(worker.id).subscribe({
        next: () => {
          // 2. Si el servidor responde OK, lo quitamos de la señal 'workers'
          // Esto hace que la tabla se actualice al instante sin recargar
          this.workers.update(currentList =>
            currentList.filter(w => w.id !== worker.id)
          );
          console.log('Worker deleted successfully');
        },
        error: (err) => {
          console.error('Error deleting worker:', err);
          alert('Could not delete the worker. Please try again.');
        }
      });
    }
  }
}
