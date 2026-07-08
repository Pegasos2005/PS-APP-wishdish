package com.wishdish.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wishdish.dtos.CreateIntentResponseDTO;
import com.wishdish.dtos.OrderResponseDTO;
import com.wishdish.dtos.ReceiptDTO;
import com.wishdish.dtos.RecentPaymentDTO;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.models.PaymentStatus;
import com.wishdish.models.PaymentTransaction;
import com.wishdish.repositories.OrderRepository;
import com.wishdish.repositories.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentTransactionRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${stripe.currency:eur}")
    private String currency;

    @Transactional
    public CreateIntentResponseDTO createIntent(Integer tableNumber) throws StripeException {
        if (tableNumber == null) {
            throw new IllegalArgumentException("tableNumber requerido");
        }

        List<Order.OrderStatus> activeStatuses = Arrays.asList(
                Order.OrderStatus.in_kitchen, Order.OrderStatus.served);
        List<Order> activeOrders = orderRepository
                .findByDiningTable_TableNumberAndStatusIn(tableNumber, activeStatuses);

        if (activeOrders.isEmpty()) {
            throw new IllegalStateException("La mesa " + tableNumber + " no tiene órdenes activas que cobrar.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Order o : activeOrders) {
            for (OrderItem item : o.getItems()) {
                BigDecimal qty = BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity());
                BigDecimal price = item.getUnitPrice() == null ? BigDecimal.ZERO : item.getUnitPrice();
                total = total.add(price.multiply(qty));
            }
        }

        total = total.setScale(2, RoundingMode.HALF_UP);
        long amountCents = total.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();

        if (amountCents <= 0) {
            throw new IllegalStateException("Importe total no válido para cobrar: " + total);
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .putMetadata("tableNumber", tableNumber.toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setStripePaymentIntentId(intent.getId());
        tx.setTableNumber(tableNumber);
        tx.setAmount(total);
        tx.setCurrency(currency);
        tx.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(tx);

        return new CreateIntentResponseDTO(
                intent.getClientSecret(),
                intent.getId(),
                amountCents,
                currency
        );
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public void confirm(String paymentIntentId) throws StripeException {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            throw new IllegalArgumentException("paymentIntentId requerido");
        }

        PaymentTransaction tx = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "PaymentTransaction no encontrada para el intent dado."));

        // Idempotencia
        if (tx.getStatus() == PaymentStatus.SUCCEEDED) {
            return;
        }

        // Verificación server-to-server contra Stripe
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(tx);
            throw new IllegalStateException(
                    "El PaymentIntent no está en estado succeeded (estado actual: " + intent.getStatus() + ").");
        }

        long expectedCents = tx.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        if (intent.getAmount() == null || intent.getAmount() != expectedCents) {
            // Mismatch grave: NO cerramos la mesa. Marcamos FAILED para que se revise manualmente.
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(tx);
            throw new IllegalStateException(
                    "El importe del intent (" + intent.getAmount() + ") no coincide con el calculado ("
                            + expectedCents + ").");
        }

        // La instantánea se captura ANTES de cerrar la mesa: al cerrarla las
        // comandas pasan a "paid" y dejan de aparecer como ticket activo.
        tx.setReceiptJson(buildReceiptJson(tx));
        tx.setStatus(PaymentStatus.SUCCEEDED);
        paymentRepository.save(tx);

        // Cierra la mesa con la lógica existente.
        orderService.closeTable(tx.getTableNumber());
    }

    // Ventana de "cobros recientes" que se muestran como aviso al personal.
    static final int RECENT_PAYMENTS_WINDOW_MINUTES = 5;

    @Transactional(readOnly = true)
    public List<RecentPaymentDTO> getRecentPayments() {
        LocalDateTime after = LocalDateTime.now().minusMinutes(RECENT_PAYMENTS_WINDOW_MINUTES);
        return paymentRepository
                .findByStatusAndUpdatedAtAfterOrderByUpdatedAtDesc(PaymentStatus.SUCCEEDED, after)
                .stream()
                .map(tx -> new RecentPaymentDTO(
                        tx.getId(), tx.getTableNumber(), tx.getAmount(), tx.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getReceiptJson(String reference) {
        return paymentRepository.findByStripePaymentIntentId(reference)
                .filter(tx -> tx.getStatus() == PaymentStatus.SUCCEEDED && tx.getReceiptJson() != null)
                .map(PaymentTransaction::getReceiptJson)
                .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado para la referencia dada."));
    }

    // El recibo es secundario al cobro: si algo falla aquí, el pago se confirma
    // igualmente y el endpoint de recibo responderá 404 para esta referencia.
    String buildReceiptJson(PaymentTransaction tx) {
        try {
            List<OrderResponseDTO> orders = orderService.getActiveOrdersByTable(tx.getTableNumber());
            ReceiptDTO receipt = new ReceiptDTO(
                    tx.getStripePaymentIntentId(),
                    tx.getTableNumber(),
                    LocalDateTime.now(),
                    tx.getAmount(),
                    tx.getCurrency(),
                    orders);
            return objectMapper.writeValueAsString(receipt);
        } catch (Exception e) {
            return null;
        }
    }
}
