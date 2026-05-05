import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-crud-menu',
  standalone: true,
  templateUrl: './crud-menu.component.html',
  styleUrls: ['./crud-menu.component.css']
})
export class CrudMenuComponent {
  private router = inject(Router);

  navigateTo(type: string) {
    if (type === 'products') {
      this.router.navigate(['/admin/product-management/product-list']);
    } else if (type === 'ingredients') {
      this.router.navigate(['/admin/ingredient-management/ingredient-list']);
    }
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }
}
