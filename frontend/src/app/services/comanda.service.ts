import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComandaService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  getComandasActivas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/active`);
  }

  // Avanza el estado de un plato (in_kitchen -> prepared)
  avanzarEstadoItem(itemId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/items/${itemId}/advance`, {});
  }

  // PE-08-04: Avanza el estado de la comanda completa (in_kitchen -> served)
  // Esto hará que desaparezca de la lista de "active"
  finalizarComanda(orderId: number): Observable<any> {
    // Apuntamos al nuevo endpoint del controlador
    return this.http.put(`${this.apiUrl}/${orderId}/advance`, {});
  }
}
