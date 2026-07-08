// src/app/shared/components/bar-chart/bar-chart.component.ts
import { Component, Input, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface BarChartPoint {
  label: string;
  amount: number;
}

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css'],
})
export class BarChartComponent {
  private pointsSignal = signal<BarChartPoint[]>([]);

  @Input({ required: true })
  set points(value: BarChartPoint[]) {
    this.pointsSignal.set(value ?? []);
  }

  // Prefijo del eje Y y los tooltips ('€' para importes, '' para conteos)
  @Input() valuePrefix = '€';

  // Límite superior "redondo" del eje Y en función del máximo de los datos
  private chartTopLimit = computed(() => {
    const data = this.pointsSignal();
    const maxAmount = Math.max(0, ...data.map(d => d.amount));
    let chartTopLimit = 10;
    if (maxAmount > 0) {
      const digits = Math.floor(Math.log10(maxAmount));
      const factor = Math.pow(10, digits - 1 >= 0 ? digits - 1 : 0);
      const step = Math.ceil(maxAmount / 4 / factor) * factor;
      chartTopLimit = step * 4;
    }
    return chartTopLimit;
  });

  yAxisLabels = computed(() => {
    const top = this.chartTopLimit();
    return [top, top * 0.75, top * 0.5, top * 0.25, 0];
  });

  bars = computed(() => {
    const top = this.chartTopLimit();
    return this.pointsSignal().map(d => ({
      ...d,
      percentage: top > 0 ? (d.amount / top) * 100 : 0
    }));
  });
}
