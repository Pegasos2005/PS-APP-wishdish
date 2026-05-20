import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-close-cash',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './close-cash.component.html',
  styleUrls: ['./close-cash.component.css']
})
export class CloseCashComponent implements OnInit {
  private router = inject(Router);

  // Signal para guardar la fecha formateada
  currentDateFormatted = signal<string>('');

  ngOnInit() {
    this.generateFormattedDate();
  }

  generateFormattedDate() {
    const today = new Date();

    // Configuramos el formateador nativo en español
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    };

    // Obtiene: "miércoles, 20/05/2026"
    let rawDate = new Intl.DateTimeFormat('es-ES', options).format(today);

    // Capitalizamos la primera letra: "Miércoles, 20/05/2026"
    const finalDate = rawDate.charAt(0).toUpperCase() + rawDate.slice(1);

    this.currentDateFormatted.set(finalDate);
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }
}
