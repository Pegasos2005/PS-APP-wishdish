// src/app/features/public/landing-access.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-access',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing-access.component.html',
  styleUrls: ['./landing-access.component.css']
})
export class LandingAccessComponent {
  // Inyección de dependencias moderna (Signal-ready)
  private router = inject(Router);

  // Funciones que se llamarán al hacer click en los botones
  joinAsAdmin(): void {
    console.log('Navegando al login de Admin');
    this.router.navigate(['/admin/dashboard']);
  }

  joinAsWorker(): void {
    console.log('Navegando a la vista del Camarero');
    this.router.navigate(['/worker/worker-view']);
  }

  joinAsUser(): void {
    console.log('Navegando al menú del cliente');
    this.router.navigate(['/customer/customer-home']);
  }
}
