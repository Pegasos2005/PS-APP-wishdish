package com.wishdish.services;

import com.wishdish.dtos.OrderResponseDTO;
import com.wishdish.dtos.RecentPaymentDTO;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.models.PaymentStatus;
import com.wishdish.models.PaymentTransaction;
import com.wishdish.models.Product;
import com.wishdish.repositories.PaymentTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentTransactionRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Spy
    private ObjectMapper objectMapper = JsonMapper.builder().build();

    @InjectMocks
    private PaymentService paymentService;

    // --- Helpers para construir datos de prueba ---

    private PaymentTransaction transaction(String intentId, Integer tableNumber, String amount) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setStripePaymentIntentId(intentId);
        tx.setTableNumber(tableNumber);
        tx.setAmount(new BigDecimal(amount));
        tx.setCurrency("eur");
        return tx;
    }

    private OrderResponseDTO orderWithItem(String productName, String unitPrice,
                                           String addedExtras, String removedDefaults) {
        Product product = new Product();
        product.setName(productName);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal(unitPrice));
        item.setAddedExtras(addedExtras);
        item.setRemovedDefaults(removedDefaults);

        Order order = new Order();
        order.setStatus(Order.OrderStatus.served);
        // orderDate no tiene setter (lo fija la BD al persistir); en tests lo forzamos por reflexión
        ReflectionTestUtils.setField(order, "orderDate", LocalDateTime.of(2026, 7, 6, 14, 30));
        item.setOrder(order);
        order.getItems().add(item);

        return new OrderResponseDTO(order);
    }

    // --- buildReceiptJson ---

    @Test
    void buildReceiptJsonGeneraLaInstantaneaConReferenciaYComandas() throws Exception {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        when(orderService.getActiveOrdersByTable(5))
                .thenReturn(List.of(orderWithItem("Pizza", "10.70", "Queso:1.00", "Cebolla")));

        String json = paymentService.buildReceiptJson(tx);

        assertNotNull(json);
        JsonNode receipt = objectMapper.readTree(json);
        assertEquals("pi_123", receipt.get("reference").asText());
        assertEquals(5, receipt.get("tableNumber").asInt());
        assertEquals(21.40, receipt.get("amount").asDouble(), 0.001);
        assertEquals("eur", receipt.get("currency").asText());
        assertFalse(receipt.get("paidAt").isNull());

        JsonNode item = receipt.get("orders").get(0).get("items").get(0);
        assertEquals("Pizza", item.get("productName").asText());
        assertEquals(2, item.get("quantity").asInt());
        assertEquals(10.70, item.get("productPrice").asDouble(), 0.001);
        assertEquals("Queso", item.get("extras").get(0).get("name").asText());
        assertEquals("Cebolla", item.get("removedDefaults").get(0).asText());
    }

    @Test
    void buildReceiptJsonDevuelveNullSiFallaLaCaptura() {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        when(orderService.getActiveOrdersByTable(5))
                .thenThrow(new RuntimeException("fallo simulado"));

        // El recibo es secundario: la captura no debe propagar el error al cobro.
        assertNull(paymentService.buildReceiptJson(tx));
    }

    // --- getReceiptJson ---

    @Test
    void getReceiptJsonDevuelveLaInstantaneaSiElPagoEstaConfirmado() {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        tx.setStatus(PaymentStatus.SUCCEEDED);
        tx.setReceiptJson("{\"reference\":\"pi_123\"}");
        when(paymentRepository.findByStripePaymentIntentId("pi_123")).thenReturn(Optional.of(tx));

        assertEquals("{\"reference\":\"pi_123\"}", paymentService.getReceiptJson("pi_123"));
    }

    @Test
    void getReceiptJsonLanzaSiNoExisteLaReferencia() {
        when(paymentRepository.findByStripePaymentIntentId("pi_no_existe")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> paymentService.getReceiptJson("pi_no_existe"));
    }

    @Test
    void getReceiptJsonLanzaSiElPagoNoEstaConfirmado() {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        tx.setStatus(PaymentStatus.PENDING);
        tx.setReceiptJson("{\"reference\":\"pi_123\"}");
        when(paymentRepository.findByStripePaymentIntentId("pi_123")).thenReturn(Optional.of(tx));

        assertThrows(IllegalArgumentException.class, () -> paymentService.getReceiptJson("pi_123"));
    }

    @Test
    void getReceiptJsonLanzaSiNoHayInstantanea() {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        tx.setStatus(PaymentStatus.SUCCEEDED);
        when(paymentRepository.findByStripePaymentIntentId("pi_123")).thenReturn(Optional.of(tx));

        assertThrows(IllegalArgumentException.class, () -> paymentService.getReceiptJson("pi_123"));
    }

    // --- getRecentPayments ---

    @Test
    void getRecentPaymentsMapeaLasTransaccionesDeLaVentana() {
        PaymentTransaction tx = transaction("pi_123", 5, "21.40");
        // id y updatedAt los fija la BD al persistir; en tests los forzamos por reflexión
        ReflectionTestUtils.setField(tx, "id", 7L);
        LocalDateTime paidAt = LocalDateTime.of(2026, 7, 6, 20, 15);
        ReflectionTestUtils.setField(tx, "updatedAt", paidAt);

        ArgumentCaptor<LocalDateTime> afterCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(paymentRepository.findByStatusAndUpdatedAtAfterOrderByUpdatedAtDesc(
                eq(PaymentStatus.SUCCEEDED), afterCaptor.capture()))
                .thenReturn(List.of(tx));

        List<RecentPaymentDTO> recent = paymentService.getRecentPayments();

        assertEquals(1, recent.size());
        assertEquals(7L, recent.get(0).getId());
        assertEquals(5, recent.get(0).getTableNumber());
        assertEquals(new BigDecimal("21.40"), recent.get(0).getAmount());
        assertEquals(paidAt, recent.get(0).getPaidAt());

        // La ventana consultada debe ser "ahora - 5 minutos" (con margen por el reloj del test)
        LocalDateTime expectedAfter = LocalDateTime.now()
                .minusMinutes(PaymentService.RECENT_PAYMENTS_WINDOW_MINUTES);
        assertTrue(Math.abs(java.time.Duration.between(expectedAfter, afterCaptor.getValue()).getSeconds()) < 5);
    }

    @Test
    void getRecentPaymentsDevuelveVacioSinCobrosRecientes() {
        when(paymentRepository.findByStatusAndUpdatedAtAfterOrderByUpdatedAtDesc(
                eq(PaymentStatus.SUCCEEDED), any(LocalDateTime.class)))
                .thenReturn(List.of());

        assertTrue(paymentService.getRecentPayments().isEmpty());
    }
}
