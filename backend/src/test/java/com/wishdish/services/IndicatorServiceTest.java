package com.wishdish.services;

import com.wishdish.dtos.RevenueReportDTO;
import com.wishdish.dtos.SlotDistributionReportDTO;
import com.wishdish.dtos.TopProductsReportDTO;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.models.Product;
import com.wishdish.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicatorServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private IndicatorService indicatorService;

    // --- Helpers para construir datos de prueba ---

    private Order paidOrder(LocalDateTime orderDate, OrderItem... items) {
        Order order = new Order();
        order.setStatus(Order.OrderStatus.paid);
        // orderDate no tiene setter (lo fija la BD al persistir); en tests lo forzamos por reflexión
        ReflectionTestUtils.setField(order, "orderDate", orderDate);
        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }
        return order;
    }

    private OrderItem item(String productName, String unitPrice) {
        Product product = new Product();
        product.setName(productName);
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal(unitPrice));
        return item;
    }

    private void mockPaidOrders(List<Order> orders) {
        when(orderRepository.findByStatusAndOrderDateAfter(eq(Order.OrderStatus.paid), any(LocalDateTime.class)))
                .thenReturn(orders);
    }

    // --- Facturación ---

    @Test
    void dailyRevenueAssignsOrdersToTheirHourBucket() {
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
        LocalDateTime fiveHoursAgo = LocalDateTime.now().minusHours(5);
        mockPaidOrders(List.of(
                paidOrder(twoHoursAgo, item("Burger", "10.00"), item("Cola", "2.50")),
                paidOrder(fiveHoursAgo, item("Burger", "10.00"))
        ));

        RevenueReportDTO report = indicatorService.getRevenue("daily");

        assertEquals("daily", report.getRange());
        assertEquals(new BigDecimal("22.50"), report.getTotal());
        assertEquals(24, report.getPoints().size());

        String labelTwoHoursAgo = String.format("%02d:00", twoHoursAgo.getHour());
        String labelFiveHoursAgo = String.format("%02d:00", fiveHoursAgo.getHour());
        assertEquals(new BigDecimal("12.50"), amountForLabel(report, labelTwoHoursAgo));
        assertEquals(new BigDecimal("10.00"), amountForLabel(report, labelFiveHoursAgo));
    }

    @Test
    void weeklyRevenueAssignsOrdersToTheirDayBucket() {
        mockPaidOrders(List.of(
                paidOrder(LocalDateTime.now(), item("Burger", "10.00")),
                paidOrder(LocalDateTime.now().minusDays(3), item("Cola", "2.50"))
        ));

        RevenueReportDTO report = indicatorService.getRevenue("weekly");

        assertEquals(new BigDecimal("12.50"), report.getTotal());
        assertEquals(7, report.getPoints().size());
        // Cronológico: el último punto es hoy, el índice 3 es hace 3 días
        assertEquals(new BigDecimal("10.00"), report.getPoints().get(6).getAmount());
        assertEquals(new BigDecimal("2.50"), report.getPoints().get(3).getAmount());
    }

    @Test
    void revenueWithoutDataReturnsZeroedBuckets() {
        mockPaidOrders(List.of());

        RevenueReportDTO report = indicatorService.getRevenue("weekly");

        assertEquals(BigDecimal.ZERO, report.getTotal());
        assertEquals(7, report.getPoints().size());
        assertTrue(report.getPoints().stream()
                .allMatch(p -> p.getAmount().compareTo(BigDecimal.ZERO) == 0));
    }

    // --- Ranking de productos ---

    @Test
    void topProductsRanksByUnitsAndAccumulatesRevenue() {
        mockPaidOrders(List.of(
                paidOrder(LocalDateTime.now(),
                        item("Burger", "10.00"), item("Burger", "10.00"), item("Burger", "10.00"),
                        item("Cola", "2.50"), item("Cola", "2.50"),
                        item("Agua", "1.50"), item("Agua", "1.50"))
        ));

        TopProductsReportDTO report = indicatorService.getTopProducts("daily");

        assertEquals(3, report.getProducts().size());
        assertEquals("Burger", report.getProducts().get(0).getName());
        assertEquals(3, report.getProducts().get(0).getUnits());
        assertEquals(new BigDecimal("30.00"), report.getProducts().get(0).getRevenue());
        // Empate a 2 unidades: desempata alfabéticamente
        assertEquals("Agua", report.getProducts().get(1).getName());
        assertEquals("Cola", report.getProducts().get(2).getName());
    }

    @Test
    void topProductsIsLimitedToTen() {
        List<OrderItem> items = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            items.add(item("Producto " + i, "5.00"));
        }
        mockPaidOrders(List.of(paidOrder(LocalDateTime.now(), items.toArray(new OrderItem[0]))));

        TopProductsReportDTO report = indicatorService.getTopProducts("weekly");

        assertEquals(10, report.getProducts().size());
    }

    // --- Distribución por franjas ---

    @Test
    void ordersBySlotCountsOrdersInTwoHourSlots() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        mockPaidOrders(List.of(
                paidOrder(yesterday.withHour(13), item("Burger", "10.00")),
                paidOrder(yesterday.withHour(13).withMinute(45), item("Cola", "2.50")),
                paidOrder(yesterday.withHour(20), item("Agua", "1.50"))
        ));

        SlotDistributionReportDTO report = indicatorService.getOrdersBySlot("weekly");

        assertEquals(3, report.getTotalOrders());
        assertEquals(12, report.getSlots().size());
        assertEquals(2, countForSlot(report, "12-14"));
        assertEquals(1, countForSlot(report, "20-22"));
        assertEquals(0, countForSlot(report, "08-10"));
    }

    @Test
    void ordersBySlotWithoutDataReturnsZeroedSlots() {
        mockPaidOrders(List.of());

        SlotDistributionReportDTO report = indicatorService.getOrdersBySlot("daily");

        assertEquals(0, report.getTotalOrders());
        assertEquals(12, report.getSlots().size());
        assertTrue(report.getSlots().stream().allMatch(s -> s.getCount() == 0));
    }

    // --- Validación de rango ---

    @Test
    void invalidRangeThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> indicatorService.getRevenue("monthly"));
        assertThrows(IllegalArgumentException.class, () -> indicatorService.getTopProducts(""));
        assertThrows(IllegalArgumentException.class, () -> indicatorService.getOrdersBySlot(null));
    }

    // --- Utilidades de aserción ---

    private BigDecimal amountForLabel(RevenueReportDTO report, String label) {
        return report.getPoints().stream()
                .filter(p -> p.getLabel().equals(label))
                .findFirst()
                .orElseThrow()
                .getAmount();
    }

    private int countForSlot(SlotDistributionReportDTO report, String slot) {
        return report.getSlots().stream()
                .filter(s -> s.getSlot().equals(slot))
                .findFirst()
                .orElseThrow()
                .getCount();
    }
}
