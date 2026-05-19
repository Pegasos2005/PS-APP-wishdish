import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CreateIntentResponse {
  clientSecret: string;
  paymentIntentId: string;
  amountCents: number;
  currency: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + 'payments';

  createIntent(tableNumber: number): Observable<CreateIntentResponse> {
    return this.http.post<CreateIntentResponse>(
      `${this.apiUrl}/create-intent`,
      { tableNumber }
    );
  }

  confirm(paymentIntentId: string): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/confirm`,
      { paymentIntentId }
    );
  }
}
