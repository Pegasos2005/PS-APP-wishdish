import { Routes } from '@angular/router';
import { Menu } from './menu/menu';
import { Pedido } from './pedido/pedido';

export const routes: Routes = [
  { path: '', component: Menu },
  { path: 'pedido', component: Pedido }
];
