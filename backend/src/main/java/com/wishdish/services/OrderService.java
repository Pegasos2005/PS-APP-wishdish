package com.wishdish.services;

import com.wishdish.dtos.OrderResponseDTO;
import com.wishdish.models.DiningTable;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.models.Product;
import com.wishdish.repositories.DiningTableRepository;
import com.wishdish.repositories.OrderItemRepository;
import com.wishdish.repositories.OrderRepository;
import com.wishdish.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // @Transactional asegura que si falla un plato, no se guarde la comanda a medias
    @Transactional
    public Order createOrder(Integer tableNumber, List<Integer> productIds) {
        // Busca mesa por su número visual, no por su ID interno
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Error: La mesa número " + tableNumber + " no existe."));

        Order newOrder = new Order();
        newOrder.setDiningTable(table);
        newOrder.setStatus(Order.OrderStatus.in_kitchen);

        Order savedOrder = orderRepository.save(newOrder);

        for (Integer productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Error: El producto " + productId + " no existe."));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(1);
            item.setStatus(OrderItem.ItemStatus.in_kitchen);

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
}