package com.wishdish.services;

import com.wishdish.dtos.*;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class IndicatorService {

    public static final String RANGE_DAILY = "daily";
    public static final String RANGE_WEEKLY = "weekly";

    private static final int TOP_PRODUCTS_LIMIT = 10;
    private static final int SLOT_SIZE_HOURS = 2;
    private static final int SLOTS_PER_DAY = 24 / SLOT_SIZE_HOURS;
    private static final String[] WEEKDAY_LABELS = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

    @Autowired
    private OrderRepository orderRepository;

    // Facturación por horas (daily) o por días (weekly)
    @Transactional(readOnly = true)
    public RevenueReportDTO getRevenue(String range) {
        List<Order> orders = findPaidOrdersInRange(range);

        BigDecimal total = BigDecimal.ZERO;
        List<ChartPointDTO> points;

        if (RANGE_DAILY.equals(range)) {
            // Cubos horarios a cero para que la gráfica no tenga huecos (mismo patrón que el daily-report)
            Map<Integer, BigDecimal> hourlySum = new HashMap<>();
            for (int i = 0; i < 24; i++) {
                hourlySum.put(LocalDateTime.now().minusHours(i).getHour(), BigDecimal.ZERO);
            }

            for (Order order : orders) {
                BigDecimal orderTotal = calculateOrderTotal(order);
                total = total.add(orderTotal);
                int hour = order.getOrderDate().getHour();
                if (hourlySum.containsKey(hour)) {
                    hourlySum.put(hour, hourlySum.get(hour).add(orderTotal));
                }
            }

            // Cronológico: desde hace 23h hasta la hora actual
            points = new ArrayList<>();
            for (int i = 23; i >= 0; i--) {
                int hour = LocalDateTime.now().minusHours(i).getHour();
                points.add(new ChartPointDTO(String.format("%02d:00", hour), hourlySum.get(hour)));
            }
        } else {
            // Cubos diarios a cero para los últimos 7 días naturales
            Map<LocalDate, BigDecimal> dailySum = new LinkedHashMap<>();
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                dailySum.put(today.minusDays(i), BigDecimal.ZERO);
            }

            for (Order order : orders) {
                BigDecimal orderTotal = calculateOrderTotal(order);
                total = total.add(orderTotal);
                LocalDate day = order.getOrderDate().toLocalDate();
                if (dailySum.containsKey(day)) {
                    dailySum.put(day, dailySum.get(day).add(orderTotal));
                }
            }

            points = new ArrayList<>();
            for (Map.Entry<LocalDate, BigDecimal> entry : dailySum.entrySet()) {
                points.add(new ChartPointDTO(formatDayLabel(entry.getKey()), entry.getValue()));
            }
        }

        RevenueReportDTO report = new RevenueReportDTO();
        report.setRange(range);
        report.setTotal(total);
        report.setPoints(points);
        return report;
    }

    // Ranking de productos más vendidos por unidades
    @Transactional(readOnly = true)
    public TopProductsReportDTO getTopProducts(String range) {
        List<Order> orders = findPaidOrdersInRange(range);

        Map<String, Integer> unitsByProduct = new HashMap<>();
        Map<String, BigDecimal> revenueByProduct = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String name = item.getProduct().getName();
                BigDecimal lineTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                unitsByProduct.merge(name, item.getQuantity(), Integer::sum);
                revenueByProduct.merge(name, lineTotal, BigDecimal::add);
            }
        }

        List<TopProductDTO> ranking = unitsByProduct.entrySet().stream()
                .map(e -> new TopProductDTO(e.getKey(), e.getValue(), revenueByProduct.get(e.getKey())))
                .sorted(Comparator.comparing(TopProductDTO::getUnits).reversed()
                        .thenComparing(TopProductDTO::getName))
                .limit(TOP_PRODUCTS_LIMIT)
                .toList();

        TopProductsReportDTO report = new TopProductsReportDTO();
        report.setRange(range);
        report.setProducts(ranking);
        return report;
    }

    // Distribución de comandas por franja horaria (franjas de 2 horas)
    @Transactional(readOnly = true)
    public SlotDistributionReportDTO getOrdersBySlot(String range) {
        List<Order> orders = findPaidOrdersInRange(range);

        int[] counts = new int[SLOTS_PER_DAY];
        for (Order order : orders) {
            counts[order.getOrderDate().getHour() / SLOT_SIZE_HOURS]++;
        }

        List<SlotCountDTO> slots = new ArrayList<>();
        for (int i = 0; i < SLOTS_PER_DAY; i++) {
            String label = String.format("%02d-%02d", i * SLOT_SIZE_HOURS, (i + 1) * SLOT_SIZE_HOURS);
            slots.add(new SlotCountDTO(label, counts[i]));
        }

        SlotDistributionReportDTO report = new SlotDistributionReportDTO();
        report.setRange(range);
        report.setTotalOrders(orders.size());
        report.setSlots(slots);
        return report;
    }

    private List<Order> findPaidOrdersInRange(String range) {
        return orderRepository.findByStatusAndOrderDateAfter(Order.OrderStatus.paid, rangeStart(range));
    }

    private LocalDateTime rangeStart(String range) {
        if (RANGE_DAILY.equals(range)) {
            return LocalDateTime.now().minusHours(24);
        }
        if (RANGE_WEEKLY.equals(range)) {
            return LocalDate.now().minusDays(6).atStartOfDay();
        }
        throw new IllegalArgumentException("Rango no válido: " + range + ". Use 'daily' o 'weekly'.");
    }

    private BigDecimal calculateOrderTotal(Order order) {
        BigDecimal orderTotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            orderTotal = orderTotal.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return orderTotal;
    }

    private String formatDayLabel(LocalDate date) {
        String weekday = WEEKDAY_LABELS[date.getDayOfWeek().getValue() - 1];
        return String.format("%s %02d/%02d", weekday, date.getDayOfMonth(), date.getMonthValue());
    }
}
