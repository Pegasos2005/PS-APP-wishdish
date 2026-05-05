package com.wishdish.services;

import com.wishdish.dtos.OrderItemRequestDTO;
import com.wishdish.dtos.OrderResponseDTO;
import com.wishdish.models.*;
import com.wishdish.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Transactional
    public Order createOrder(Integer tableNumber, List<OrderItemRequestDTO> items) {
        // Busca mesa por su número visual
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Error: La mesa número " + tableNumber + " no existe."));

        Order newOrder = new Order();
        newOrder.setDiningTable(table);
        newOrder.setStatus(Order.OrderStatus.in_kitchen);

        Order savedOrder = orderRepository.save(newOrder);

        for (OrderItemRequestDTO itemRequest : items) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Error: El producto " + itemRequest.getProductId() + " no existe."));

            // 1. Calculamos el precio y procesamos los textos UNA sola vez
            BigDecimal precioCalculado = product.getPrice();
            StringBuilder notes = new StringBuilder();
            StringBuilder extrasGuardados = new StringBuilder();

            // Procesar extras
            if (itemRequest.getAddedExtras() != null && !itemRequest.getAddedExtras().isEmpty()) {
                notes.append("Extra: ").append(String.join(", ", itemRequest.getAddedExtras())).append(". ");

                for (String nombreExtra : itemRequest.getAddedExtras()) {
                    Ingredient ingredient = ingredientRepository.findByName(nombreExtra).orElse(null);
                    BigDecimal extraPrice = BigDecimal.ZERO;

                    if (ingredient != null && ingredient.getExtraPrice() != null) {
                        extraPrice = ingredient.getExtraPrice();
                        precioCalculado = precioCalculado.add(extraPrice);
                    }

                    if (extrasGuardados.length() > 0) {
                        extrasGuardados.append(";");
                    }
                    extrasGuardados.append(nombreExtra).append(":").append(extraPrice);
                }
            }

            // Procesar eliminaciones
            String removedDefaultsStr = "";
            if (itemRequest.getRemovedDefaults() != null && !itemRequest.getRemovedDefaults().isEmpty()) {
                notes.append("Sin: ").append(String.join(", ", itemRequest.getRemovedDefaults())).append(".");
                removedDefaultsStr = String.join(";", itemRequest.getRemovedDefaults());
            }

            String finalNotes = notes.toString().trim();
            String finalAddedExtras = extrasGuardados.toString();

            // 2. MAGIA: Guardamos un item en base de datos por cada unidad pedida
            for (int i = 0; i < itemRequest.getQuantity(); i++) {
                OrderItem item = new OrderItem();
                item.setOrder(savedOrder);
                item.setProduct(product);
                item.setQuantity(1); // Ahora forzamos que cada línea sea 1 unidad
                item.setStatus(OrderItem.ItemStatus.in_kitchen);

                item.setRemovedDefaults(removedDefaultsStr);
                item.setAddedExtras(finalAddedExtras);
                item.setUnitPrice(precioCalculado);
                item.setObservations(finalNotes);

                orderItemRepository.save(item);
            }
        }

        return orderRepository.findById(savedOrder.getId()).orElseThrow();
    }

    public List<OrderResponseDTO> getActiveOrders() {
        List<Order> orders = orderRepository.findByStatusIn(Collections.singletonList(Order.OrderStatus.in_kitchen));

        return orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderItem advanceItemStatus(Integer itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Error: El item " + itemId + " no existe."));

        item.advanceStatus();
        orderItemRepository.save(item);

        checkAndAdvanceOrder(item.getOrder());

        return item;
    }

    @Transactional
    public Order advanceOrderStatus(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Error: La orden " + orderId + " no existe."));

        order.setStatus(Order.OrderStatus.served);
        return orderRepository.save(order);
    }

    private void checkAndAdvanceOrder(Order order) {
        boolean allPrepared = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItem.ItemStatus.prepared);

        if (allPrepared && order.getStatus() == Order.OrderStatus.in_kitchen) {
            order.advanceStatus();
            orderRepository.save(order);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getActiveOrdersByTable(Integer tableNumber) {
        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);

        List<Order> activeOrders = orderRepository.findByDiningTable_TableNumberAndStatusIn(tableNumber, activeStatuses);

        return activeOrders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void requestPayment(Integer tableNumber) {
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa " + tableNumber + " no existe."));
        table.setPaymentRequested(true);
        diningTableRepository.save(table);
    }

    @Transactional
    public void closeTable(Integer tableNumber) {
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa " + tableNumber + " no existe."));

        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);
        List<Order> activeOrders = orderRepository.findByDiningTable_TableNumberAndStatusIn(tableNumber, activeStatuses);
        for (Order o : activeOrders) {
            o.setStatus(Order.OrderStatus.paid);
        }
        orderRepository.saveAll(activeOrders);

        table.setPaymentRequested(false);
        diningTableRepository.save(table);
    }

    @Transactional(readOnly = true)
    public List<Integer> getTablesAwaitingPayment() {
        return diningTableRepository.findByPaymentRequestedTrue().stream()
                .map(DiningTable::getTableNumber)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean tableHasActiveOrders(Integer tableNumber) {
        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);
        return !orderRepository.findByDiningTable_TableNumberAndStatusIn(tableNumber, activeStatuses).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean isPaymentRequested(Integer tableNumber) {
        return diningTableRepository.findByTableNumber(tableNumber)
                .map(DiningTable::isPaymentRequested)
                .orElse(false);
    }
}