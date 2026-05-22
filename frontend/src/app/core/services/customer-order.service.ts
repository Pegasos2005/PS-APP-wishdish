import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subscription, interval, switchMap } from 'rxjs';
import { OrderItem } from '../interfaces/order-item.interface'; // Fíjate en el nuevo nombre
import { environment } from '../../../environments/environment';

export interface TableStatus {
  paymentRequested: boolean;
  hasActiveOrders: boolean;
  reassignTo: number | null;
}

export interface TableOccupancy {
  tableNumber: number;
  occupied: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class CustomerOrderService {
  private apiUrl = environment.apiUrl + 'orders';

  tableId = signal<number | null>(null);
  tableClosed = signal<boolean>(false);

  order: OrderItem[] = [];
  private _totalItems = signal(0);
  totalItems = computed(() => this._totalItems());

  private statusPollSub?: Subscription;

  constructor(private http: HttpClient) {
    const stored = sessionStorage.getItem('tableId');
    if (stored) {
      this.tableId.set(+stored);
      this.startStatusPolling();
    }
  }

  setTableId(id: number | null) {
    this.tableId.set(id);
    if (id === null) {
      sessionStorage.removeItem('tableId');
      this.stopStatusPolling();
    } else {
      sessionStorage.setItem('tableId', String(id));
      this.startStatusPolling();
    }
  }

  // Polling singleton: detecta paymentRequested, cierre y reasignación.
  // Cualquier vista cliente que use tableId() como signal recibe los cambios.
  private startStatusPolling(): void {
    this.stopStatusPolling();
    this.statusPollSub = interval(3000)
      .pipe(switchMap(() => {
        const id = this.tableId();
        if (id === null) throw new Error('no table');
        return this.getTableStatus(id);
      }))
      .subscribe({
        next: (status) => this.handleStatus(status),
        error: () => { /* mesa nula o fallo de red: ignoramos hasta próximo tick */ }
      });
  }

  private stopStatusPolling(): void {
    this.statusPollSub?.unsubscribe();
    this.statusPollSub = undefined;
  }

  private handleStatus(status: TableStatus): void {
    const currentId = this.tableId();
    if (currentId === null) return;

    if (status.reassignTo !== null && status.reassignTo !== undefined) {
      const newId = status.reassignTo;
      this.acknowledgeReassign(currentId).subscribe({
        next: () => this.setTableId(newId),
        error: (err) => console.error('Error confirmando reasignación:', err)
      });
      return;
    }

    if (sessionStorage.getItem('paymentRequested') === 'true'
        && !status.paymentRequested
        && !status.hasActiveOrders) {
      sessionStorage.removeItem('paymentRequested');
      this.tableClosed.set(true);
    }
  }

  consumeTableClosed(): boolean {
    if (this.tableClosed()) {
      this.tableClosed.set(false);
      return true;
    }
    return false;
  }

  joinOrCreateTable(tableNumber: number): Observable<void> {
      // Asegúrate de que 'this.apiUrl' sea la ruta correcta a tu backend (ej. http://localhost:8080/api)
      return this.http.post<void>(`${environment.apiUrl}tables/${tableNumber}/join`, {});
  }

  getTicketByTable(tableId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/table/${tableId}`);
  }

  requestPayment(tableNumber: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${tableNumber}/request-payment`, {});
  }

  cancelPaymentRequest(tableNumber: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${tableNumber}/cancel-payment`, {});
  }

  getTableStatus(tableNumber: number): Observable<TableStatus> {
    return this.http.get<TableStatus>(
      `${environment.apiUrl}tables/${tableNumber}/status`
    );
  }

  acknowledgeReassign(fromTable: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${fromTable}/ack-reassign`, {});
  }

  reassignTable(fromTable: number, toTable: number): Observable<void> {
    return this.http.post<void>(
      `${environment.apiUrl}tables/${fromTable}/reassign?to=${toTable}`,
      {}
    );
  }

  getOccupancy(): Observable<TableOccupancy[]> {
    return this.http.get<TableOccupancy[]>(`${environment.apiUrl}tables/occupancy`);
  }

  closeTable(tableNumber: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${tableNumber}/close`, {});
  }

  getTablesAwaitingPayment(): Observable<number[]> {
    return this.http.get<number[]>(`${environment.apiUrl}tables/payment-requested`);
  }

  // 1. EL BOTÓN DE PAGAR (Envía a la base de datos)
  crearPedido(pedido: any): Observable<any> {
    return this.http.post(this.apiUrl, pedido);
  }

  // Funcion auxiliar: convierte la lista de ingredientes en un texto ordenado para poder comparar
  private getIngredientsSignature(product: any): string {
    const extras = product.addedExtras ? [...product.addedExtras].sort().join('|') : '';
    const removed = product.removedDefaults ? [...product.removedDefaults].sort().join('|') : '';
    const note = product.itemNotes ? product.itemNotes : '';

    return extras + '###' + removed + '###' + note;
  }

  // 2. GESTIÓN DEL CARRITO EN MEMORIA
  addProduct(productToAdd: any) {

    const signatureToAdd = this.getIngredientsSignature(productToAdd);

    const existingItem = this.order.find(item =>
      item.product.id === productToAdd.id &&
      this.getIngredientsSignature(item.product) === signatureToAdd
    );

    if (existingItem) {
      existingItem.quantity++;
    } else {
      // hacemos copia profunda del producto para evitar referencias cruzadas
      const newProduct = JSON.parse(JSON.stringify(productToAdd));
      this.order.push({ product: newProduct, quantity: 1 });
    }
    this._totalItems.update(val => val + 1);
  }

  decreaseProduct(productToRemove: any) {

    const signatureToRemove = this.getIngredientsSignature(productToRemove);

    const index = this.order.findIndex(item =>
      item.product.id === productToRemove.id && this.getIngredientsSignature(item.product) === signatureToRemove
    );

    if (index !== -1) {
      this.order[index].quantity--;
      this._totalItems.update(val => val - 1);
      if (this.order[index].quantity === 0) {
        this.order.splice(index, 1);
      }
    }
  }

  clear() {
    this.order = [];
    this._totalItems.set(0);
  }

  getSelectedProducts(): OrderItem[] {
    return this.order;
  }

  getTotalPrice(): number {
    return this.order.reduce((total, item) => {
      // Cogemos el precio con extras (si lo hay) o el precio base
      const priceToUse = item.product.calculatedPrice || item.product.price;

      // Lo multiplicamos por la cantidad de ese mismo plato y lo sumamos al total
      return total + (Number(priceToUse) * item.quantity);
    }, 0);
  }

}
