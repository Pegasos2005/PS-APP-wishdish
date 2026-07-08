import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IndicatorService, IndicatorRange, RevenueReport, TopProductsReport, SlotDistributionReport } from '../../../core/services/indicator.service';
import { BarChartComponent, BarChartPoint } from '../../../shared/components/bar-chart/bar-chart.component';

@Component({
  selector: 'app-indicators',
  standalone: true,
  imports: [CommonModule, BarChartComponent],
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit {
  private router = inject(Router);
  private indicatorService = inject(IndicatorService);

  // Cabecera
  currentDateFormatted = signal<string>('');

  // Rango seleccionado, compartido por todos los bloques del panel
  range = signal<IndicatorRange>('daily');

  // Bloque de facturación: cada bloque gestiona su propio estado
  // para que un fallo no afecte al resto del panel
  revenueLoading = signal<boolean>(true);
  revenueError = signal<boolean>(false);
  revenue = signal<RevenueReport | null>(null);

  revenueIsEmpty = computed(() => {
    const report = this.revenue();
    return report !== null && report.total === 0;
  });

  revenueChartPoints = computed<BarChartPoint[]>(() => this.revenue()?.points ?? []);

  // Bloque de ranking de productos
  topProductsLoading = signal<boolean>(true);
  topProductsError = signal<boolean>(false);
  topProducts = signal<TopProductsReport | null>(null);

  topProductsIsEmpty = computed(() => {
    const report = this.topProducts();
    return report !== null && report.products.length === 0;
  });

  // Bloque de distribución por franjas horarias
  slotsLoading = signal<boolean>(true);
  slotsError = signal<boolean>(false);
  slots = signal<SlotDistributionReport | null>(null);

  slotsIsEmpty = computed(() => {
    const report = this.slots();
    return report !== null && report.totalOrders === 0;
  });

  slotsChartPoints = computed<BarChartPoint[]>(() =>
    (this.slots()?.slots ?? []).map(s => ({ label: s.slot, amount: s.count }))
  );

  ngOnInit() {
    this.generateFormattedDate();
    this.loadAll();
  }

  setRange(range: IndicatorRange) {
    if (this.range() === range) return;
    this.range.set(range);
    this.loadAll();
  }

  loadAll() {
    this.loadRevenue();
    this.loadTopProducts();
    this.loadSlots();
  }

  loadRevenue() {
    this.revenueLoading.set(true);
    this.revenueError.set(false);
    this.indicatorService.getRevenue(this.range()).subscribe({
      next: (data) => {
        this.revenue.set(data);
        this.revenueLoading.set(false);
      },
      error: (err) => {
        console.error("Error cargando la facturación", err);
        this.revenueError.set(true);
        this.revenueLoading.set(false);
      }
    });
  }

  loadTopProducts() {
    this.topProductsLoading.set(true);
    this.topProductsError.set(false);
    this.indicatorService.getTopProducts(this.range()).subscribe({
      next: (data) => {
        this.topProducts.set(data);
        this.topProductsLoading.set(false);
      },
      error: (err) => {
        console.error("Error cargando el ranking de productos", err);
        this.topProductsError.set(true);
        this.topProductsLoading.set(false);
      }
    });
  }

  loadSlots() {
    this.slotsLoading.set(true);
    this.slotsError.set(false);
    this.indicatorService.getOrdersBySlot(this.range()).subscribe({
      next: (data) => {
        this.slots.set(data);
        this.slotsLoading.set(false);
      },
      error: (err) => {
        console.error("Error cargando la distribución por franjas", err);
        this.slotsError.set(true);
        this.slotsLoading.set(false);
      }
    });
  }

  generateFormattedDate() {
    const today = new Date();
    const dateOptions: Intl.DateTimeFormatOptions = { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' };
    let rawDate = new Intl.DateTimeFormat('es-ES', dateOptions).format(today);
    this.currentDateFormatted.set(rawDate.charAt(0).toUpperCase() + rawDate.slice(1));
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }
}
