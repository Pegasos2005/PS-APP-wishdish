import { Routes } from '@angular/router';
import { Menu } from './menu/menu';
import { Pedido } from './pedido/pedido';
import { OrderList } from './order-list/order-list';
import { VistaCamarero } from './vista-camarero/vista-camarero';

export const routes: Routes = [
  { path: '', component: Menu },
  { path: 'pedido', component: Pedido },
  { path: 'summary', component: OrderList },
  { path: 'camarero', component: VistaCamarero }
];
