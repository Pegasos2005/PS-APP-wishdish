import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface WorkerItem {
  id?: number;
  name: string;
  role: string;
  pin?: string;
  active?: boolean;
}

@Injectable({
  providedIn: 'root',
})

export class WorkerService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + 'workers';

  getWorkers(): Observable<WorkerItem[]> {
    return this.http.get<WorkerItem[]>(this.API_URL);
  }

  createWorker(worker: WorkerItem): Observable<WorkerItem> {
    return this.http.post<WorkerItem>(this.API_URL, worker);
  }

  updateWorker(id: number, worker: WorkerItem): Observable<WorkerItem> {
    return this.http.put<WorkerItem>(`${this.API_URL}/${id}`, worker);
  }

  deleteWorker(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
