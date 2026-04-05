import { Routes } from '@angular/router';
import { Pedido } from './pedido/pedido';
import { OrderList } from './order-list/order-list'; // Ajusta la ruta a tu archivo
import { Menu } from './menu/menu';

export const routes: Routes = [
  // Ruta por defecto (por ahora, se cambiará posteriormente para que redirija a la
  // pagina inicial.
  { path: '', component: Menu },

  { path: 'pedido', component: Pedido },
  { path: 'summary', component: OrderList}
];
