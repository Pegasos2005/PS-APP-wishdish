import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KitchenOrdersSystemService {
  private apiUrl = environment.apiUrl + 'orders';

  constructor(private http: HttpClient) {}

  // Pregunta por los tickets que hay que cocinar hoy
  getComandasActivas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/active`);
  }

  // Avisa de que una hamburguesa en concreto ya está hecha
  avanzarEstadoItem(itemId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/items/${itemId}/advance`, {});
  }

  // Avisa de que toda la mesa ya tiene su comida lista
  finalizarComanda(orderId: number): Observable<any> {
    // Apuntamos al nuevo endpoint del controlador
    return this.http.put(`${this.apiUrl}/${orderId}/advance`, {});
  }
}
