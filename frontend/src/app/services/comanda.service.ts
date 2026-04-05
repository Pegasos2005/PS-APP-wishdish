import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ComandaResponseDTO } from '../models/comanda.model';

@Injectable({
  providedIn: 'root'
})
export class ComandaService {
  private apiUrl = 'http://localhost:8080/api/comandas';

  constructor(private http: HttpClient) {}

  getComandasActivas(): Observable<ComandaResponseDTO[]> {
    return this.http.get<ComandaResponseDTO[]>(`${this.apiUrl}/activas`);
  }

  crearComanda(request: { numeroMesa: number, productosIds: number[] }): Observable<any> {
    return this.http.post(this.apiUrl, request);
  }

  avanzarEstadoItem(itemId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/items/${itemId}/avanzar`, {});
  }
}
