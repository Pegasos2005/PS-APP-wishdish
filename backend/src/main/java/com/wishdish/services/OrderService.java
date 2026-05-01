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

    // @Transactional asegura que si falla un plato, no se guarde la comanda a medias
    @Transactional
    public Order createOrder(Integer tableNumber, List<OrderItemRequestDTO> items) {
        // Busca mesa por su número visual, no por su ID interno
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Error: La mesa número " + tableNumber + " no existe."));

        Order newOrder = new Order();
        newOrder.setDiningTable(table);
        newOrder.setStatus(Order.OrderStatus.in_kitchen);

        Order savedOrder = orderRepository.save(newOrder);

        for (OrderItemRequestDTO itemRequest : items) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Error: El producto " + itemRequest.getProductId() + " no existe."));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);

            item.setQuantity(itemRequest.getQuantity());
            item.setStatus(OrderItem.ItemStatus.in_kitchen);


            // Precio base del plato
            BigDecimal precioCalculado = product.getPrice();

            // Traducimos las listas a un texto para la cocina
            StringBuilder notes = new StringBuilder();

            // si hay extras los sumamos
            if (itemRequest.getAddedExtras() != null && !itemRequest.getAddedExtras().isEmpty()) {
                notes.append("Extra: ").append(String.join(", ", itemRequest.getAddedExtras())).append(". ");

                // Buscamos cada ingrediente extra en la base de datos para saber su precio
                for (String nombreExtra: itemRequest.getAddedExtras()) {

                    Ingredient ingredient = ingredientRepository.findByName(nombreExtra).orElse(null);

                    if (ingredient != null && ingredient.getExtraPrice() != null) {
                        precioCalculado = precioCalculado.add(ingredient.getExtraPrice());
                    }
                }
            }

            if (itemRequest.getRemovedDefaults() != null && !itemRequest.getRemovedDefaults().isEmpty()) {
                notes.append("Sin: ").append(String.join(", ", itemRequest.getRemovedDefaults())).append(".");
            }

            // Guardamos el precio total en la linea de la comanda
            item.setUnitPrice(precioCalculado);
            // Guardamos el texto en el plato
            item.setObservations(notes.toString().trim());

            orderItemRepository.save(item);
        }

        return orderRepository.findById(savedOrder.getId()).get();
    }

    public List<OrderResponseDTO> getActiveOrders() {
        // CORRECCIÓN: Filtramos para que SOLO devuelva las que están en cocina.
        // Si incluimos 'served', volverán a aparecer al recargar la vista.
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

        // Comprobamos mágicamente si la comanda entera ha terminado
        checkAndAdvanceOrder(item.getOrder());

        return item;
    }

    // Método para forzar el avance de la comanda completa desde el controlador
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
            order.advanceStatus(); // Pasará a 'served' automáticamente
            orderRepository.save(order);
        }
    }

    // Método auxiliar (si no lo tienes ya) para no devolver la entidad Order
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getActiveOrdersByTable(Integer tableNumber) {
        // CORRECCIÓN DE ESTADOS: Queremos las que se están cocinando y las servidas.
        // (En tu código habías puesto "paid" en lugar de "served", lo que hacía que
        // desaparecieran al cocinarlas).
        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);

        List<Order> activeOrders = orderRepository.findByDiningTable_TableNumberAndStatusIn(tableNumber, activeStatuses);

        // Usamos directamente tu constructor del DTO. ¡Mágia de Java 8!
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