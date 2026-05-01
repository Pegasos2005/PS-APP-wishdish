// src/app/core/services/product.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

import { Product, ProductDTO } from '../../features/admin/interfaces/product.interface';
import { Ingredient } from '../../features/admin/interfaces/ingredient.interface';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);

  // URLS
  private readonly API_URL = environment.apiUrl + 'products';
  private readonly INGREDIENTS_URL = environment.apiUrl + 'ingredients'; // Faltaba esto

  getProducts(): Observable<ProductDTO[]> {
    return this.http.get<ProductDTO[]>(this.API_URL);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/${id}`);
  }

  updateProduct(id: number, data: Product): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/${id}`, data);
  }

  createProduct(data: Product): Observable<Product> {
    return this.http.post<Product>(this.API_URL, data);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  createIngredient(data: Ingredient): Observable<Ingredient> {
    return this.http.post<Ingredient>(this.INGREDIENTS_URL, data);
  }
}
