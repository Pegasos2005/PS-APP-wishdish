import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type IndicatorRange = 'daily' | 'weekly';

export interface ChartPoint {
  label: string;
  amount: number;
}

export interface RevenueReport {
  range: string;
  total: number;
  points: ChartPoint[];
}

export interface TopProduct {
  name: string;
  units: number;
  revenue: number;
}

export interface TopProductsReport {
  range: string;
  products: TopProduct[];
}

export interface SlotCount {
  slot: string;
  count: number;
}

export interface SlotDistributionReport {
  range: string;
  totalOrders: number;
  slots: SlotCount[];
}

@Injectable({ providedIn: 'root' })
export class IndicatorService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + 'indicators';

  getRevenue(range: IndicatorRange): Observable<RevenueReport> {
    return this.http.get<RevenueReport>(`${this.apiUrl}/revenue`, { params: { range } });
  }

  getTopProducts(range: IndicatorRange): Observable<TopProductsReport> {
    return this.http.get<TopProductsReport>(`${this.apiUrl}/top-products`, { params: { range } });
  }

  getOrdersBySlot(range: IndicatorRange): Observable<SlotDistributionReport> {
    return this.http.get<SlotDistributionReport>(`${this.apiUrl}/orders-by-slot`, { params: { range } });
  }
}
