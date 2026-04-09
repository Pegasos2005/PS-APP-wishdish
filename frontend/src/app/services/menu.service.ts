import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MenuCategoria } from '../models/menu-categoria.model';
import { environment } from '../../environments/environment';

/**
 * Servicio para obtener el menú desde el backend.
 */
@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private readonly API_URL = environment.apiUrl + 'menu';

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
  getAvailableMenu(): Observable<MenuCategoria[]> {
    return this.http.get<MenuCategoria[]>(this.API_URL + 'menu/available');
  }
}
