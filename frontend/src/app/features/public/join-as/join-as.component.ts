// src/app/features/public/join-as/join-as.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-join-as',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './join-as.component.html',
  styleUrls: ['./join-as.component.css']
})
export class JoinAsComponent {
  // Inyección del Router para navegación programática
  private router = inject(Router);

  joinAsAdmin(): void {
    this.router.navigate(['/admin']);
  }

  joinAsWorker(): void {
    this.router.navigate(['/worker']);
  }

  joinAsUser(): void {
    this.router.navigate(['/customer']);
  }
}
