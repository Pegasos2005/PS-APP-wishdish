import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MenuCategoria } from '../models/menu-categoria.model';

/**
 * Servicio para obtener el menú desde el backend.
 */
@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private readonly API_URL = 'http://localhost:8080/api/menu';

  constructor(private http: HttpClient) { }

  /**
   * Obtiene el menú completo del backend.
   * @returns Observable con array de categorías y sus productos
   */
  getMenu(): Observable<MenuCategoria[]> {
    return this.http.get<MenuCategoria[]>(this.API_URL);
  }

  /**
   * Obtiene solo productos disponibles.
   * @returns Observable con array de categorías con productos disponibles
   */
  getMenuDisponible(): Observable<MenuCategoria[]> {
    return this.http.get<MenuCategoria[]>(`${this.API_URL}/disponibles`);
  }
}
