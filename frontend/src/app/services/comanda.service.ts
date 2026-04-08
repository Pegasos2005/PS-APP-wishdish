import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ComandaResponseDTO } from '../models/comanda.model';

@Injectable({
  providedIn: 'root'
})
export class ComandaService {
  // La base es /api/orders
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  /**
   * Coincide con @GetMapping("/active") en Java
   */
  getComandasActivas(): Observable<ComandaResponseDTO[]> {
    return this.http.get<ComandaResponseDTO[]>(`${this.apiUrl}/active`);
  }

  /**
   * Coincide con @PostMapping en Java
   */
  crearComanda(request: any): Observable<any> {
    return this.http.post(this.apiUrl, request);
  }

  /**
   * Coincide con @PutMapping("/items/{itemId}/advance") en Java
   * OJO: He cambiado 'avanzar' por 'advance' para que coincida con tu OrderController
   */
  avanzarEstadoItem(itemId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/items/${itemId}/advance`, {});
  }
}
