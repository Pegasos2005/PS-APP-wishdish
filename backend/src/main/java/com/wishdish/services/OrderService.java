package com.wishdish.services;

import com.wishdish.dtos.*;
import com.wishdish.models.*;
import com.wishdish.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
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

    public boolean hasActiveTables() {
        // Buscamos si hay órdenes cocinándose (in_kitchen) o entregadas pero sin pagar (served)
        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);

        // Usamos el repositorio para ver si cuenta alguna comanda en estos estados
        List<Order> activeOrders = orderRepository.findByStatusIn(activeStatuses);

        // Si la lista no está vacía, significa que hay mesas abiertas
        return !activeOrders.isEmpty();
    }

    @Transactional(readOnly = true)
    public DailyReportDTO getDailyReport() {
        // 1. Calculamos la barrera de tiempo: Hace exactamente 24 horas
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        // 2. Buscamos todas las comandas que estén PAGADAS y sean recientes
        List<Order> recentOrders = orderRepository.findByStatusAndOrderDateAfter(Order.OrderStatus.paid, last24Hours);

        BigDecimal totalSales = BigDecimal.ZERO;
        int totalTransactions = recentOrders.size();

        // 3. Preparamos un mapa con las últimas 24 horas a cero (Para que el gráfico no tenga huecos vacíos)
        Map<Integer, BigDecimal> hourlySum = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            int hour = LocalDateTime.now().minusHours(i).getHour();
            hourlySum.put(hour, BigDecimal.ZERO);
        }

        // 4. Sumamos el precio de cada comanda en su hora correspondiente
        for (Order order : recentOrders) {
            BigDecimal orderTotal = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                orderTotal = orderTotal.add(itemTotal);
            }
            totalSales = totalSales.add(orderTotal);

            int orderHour = order.getOrderDate().getHour();
            if (hourlySum.containsKey(orderHour)) {
                hourlySum.put(orderHour, hourlySum.get(orderHour).add(orderTotal));
            }
        }

        // 5. Calculamos el ticket medio
        BigDecimal averageOrder = BigDecimal.ZERO;
        if (totalTransactions > 0) {
            averageOrder = totalSales.divide(new BigDecimal(totalTransactions), 2, RoundingMode.HALF_UP);
        }

        // 6. Formateamos la lista para Angular (Cronológicamente: desde hace 23h hasta la hora actual)
        List<HourlySalesDTO> hourlyData = new ArrayList<>();
        for (int i = 23; i >= 0; i--) {
            LocalDateTime h = LocalDateTime.now().minusHours(i);
            int hourKey = h.getHour();
            String hourString = String.format("%02d:00", hourKey);
            hourlyData.add(new HourlySalesDTO(hourString, hourlySum.get(hourKey)));
        }

        // ---> NUEVO: Mapeamos las órdenes reales de la DB a su DTO de respuesta <---
        List<OrderResponseDTO> orderDTOs = recentOrders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());

        DailyReportDTO report = new DailyReportDTO();
        report.setTotalSales(totalSales);
        report.setTotalTransactions(totalTransactions);
        report.setAverageOrder(averageOrder);
        report.setHourlyData(hourlyData);
        report.setOrders(orderDTOs);

        return report;
    }

    @Transactional
    public Order createOrder(Integer tableNumber, List<OrderItemRequestDTO> items, String generalNotes) {
        // Busca mesa por su número visual
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Error: La mesa número " + tableNumber + " no existe."));

        Order newOrder = new Order();
        newOrder.setDiningTable(table);
        newOrder.setStatus(Order.OrderStatus.in_kitchen);

        newOrder.setGeneralNotes(generalNotes);

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
                item.setItemNotes(itemRequest.getItemNotes());

                orderItemRepository.save(item);
            }
        }

        return orderRepository.findById(savedOrder.getId()).orElseThrow();
    }

    public List<OrderResponseDTO> getActiveOrders() {
        List<Order.OrderStatus> activeStatuses = Arrays.asList(
                Order.OrderStatus.in_kitchen // Solo enviamos las que están pendientes en cocina
        );

        List<Order> orders = orderRepository.findByStatusIn(activeStatuses);

        return orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    // NUEVO MÉTODO: Limpieza absoluta de caja tras el cierre
    @Transactional
    public void clearAllOrdersAndItems() {
        System.out.println("⚠️ Iniciando vaciado de caja definitivo...");

        // 1. Primero borramos los ítems obligatoriamente por la clave foránea
        orderItemRepository.deleteAllInBatch();

        // 2. Ahora que los platos están borrados, podemos limpiar las comandas
        orderRepository.deleteAllInBatch();

        System.out.println("✅ Caja limpia. Listo para el siguiente turno.");
    }

    @Transactional
    public OrderItem updateItemStatus(Integer itemId, String statusName) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Error: El item " + itemId + " no existe."));

        // Cambiamos al estado específico (prepared o in_kitchen)
        item.setStatus(OrderItem.ItemStatus.valueOf(statusName));
        orderItemRepository.save(item);

        // Sincronizar el estado de la comanda global:
        // Si todos están preparados -> served. Si hay alguno pendiente -> in_kitchen.
        Order order = item.getOrder();
        boolean allPrepared = order.getItems().stream()
                .allMatch(i -> i.getStatus() == OrderItem.ItemStatus.prepared);

        if (allPrepared) {
            order.setStatus(Order.OrderStatus.served);
        } else if (order.getStatus() == Order.OrderStatus.served) {
            order.setStatus(Order.OrderStatus.in_kitchen);
        }
        orderRepository.save(order);

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
    public void cancelPaymentRequest(Integer tableNumber) {
        DiningTable table = diningTableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Mesa " + tableNumber + " no existe."));
        table.setPaymentRequested(false);
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

    @Transactional(readOnly = true)
    public Integer getPendingReassignTo(Integer tableNumber) {
        return diningTableRepository.findByTableNumber(tableNumber)
                .map(DiningTable::getPendingReassignTo)
                .orElse(null);
    }

    // Mueve órdenes activas y paymentRequested de la mesa origen a la destino,
    // y deja un flag pendingReassignTo en la origen para que la tablet del
    // cliente lo lea por polling y actualice su tableId sola.
    @Transactional
    public void reassignTable(Integer fromNumber, Integer toNumber) {
        if (fromNumber == null || toNumber == null) {
            throw new RuntimeException("Mesa origen y destino son obligatorias.");
        }
        if (fromNumber.equals(toNumber)) {
            throw new RuntimeException("La mesa origen y destino no pueden coincidir.");
        }

        DiningTable from = diningTableRepository.findByTableNumber(fromNumber)
                .orElseThrow(() -> new RuntimeException("Mesa origen " + fromNumber + " no existe."));
        DiningTable to = diningTableRepository.findByTableNumber(toNumber)
                .orElseThrow(() -> new RuntimeException("Mesa destino " + toNumber + " no existe."));

        if (tableHasActiveOrders(toNumber) || to.isPaymentRequested()) {
            throw new RuntimeException("La mesa destino " + toNumber + " está ocupada.");
        }

        List<Order.OrderStatus> activeStatuses = Arrays.asList(Order.OrderStatus.in_kitchen, Order.OrderStatus.served);
        List<Order> activeOrders = orderRepository.findByDiningTable_TableNumberAndStatusIn(fromNumber, activeStatuses);
        for (Order o : activeOrders) {
            o.setDiningTable(to);
        }
        orderRepository.saveAll(activeOrders);

        if (from.isPaymentRequested()) {
            to.setPaymentRequested(true);
            from.setPaymentRequested(false);
        }

        from.setPendingReassignTo(toNumber);
        diningTableRepository.save(from);
        diningTableRepository.save(to);
    }

    @Transactional
    public void acknowledgeReassign(Integer fromNumber) {
        DiningTable from = diningTableRepository.findByTableNumber(fromNumber)
                .orElseThrow(() -> new RuntimeException("Mesa " + fromNumber + " no existe."));
        from.setPendingReassignTo(null);
        diningTableRepository.save(from);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOccupancy() {
        return diningTableRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(a.getTableNumber(), b.getTableNumber()))
                .map(t -> {
                    boolean occupied = tableHasActiveOrders(t.getTableNumber()) || t.isPaymentRequested();
                    Map<String, Object> entry = new java.util.LinkedHashMap<>();
                    entry.put("tableNumber", t.getTableNumber());
                    entry.put("occupied", occupied);
                    return entry;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderResponseDTO> addManualItemToOrder(Integer orderId, ManualItemRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Error: La comanda " + orderId + " no existe."));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Error: El producto " + request.getProductId() + " no existe."));


        // Si el producto no está disponible
        if (product.getAvailable() == null || !product.getAvailable()) {
            throw new RuntimeException("This product is currently unavailable");
        }

        // ESCENARIO ALTERNATIVO: Marca visual para que la cocina distinga la adición
        String adminNotes = (request.getObservations() != null ? request.getObservations().trim() + " " : "");
        String finalObservations = adminNotes + "[Añadido por personal]";

        // 1 registro por unidad pedida
        int qty = request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1;

        for (int i = 0; i < qty; i++) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(1);
            item.setStatus(OrderItem.ItemStatus.in_kitchen); // Directo a la pantalla del cocinero
            item.setUnitPrice(product.getPrice());

            item.setObservations(finalObservations.trim());
            item.setItemNotes("");
            item.setAddedExtras("");
            item.setRemovedDefaults("");

            orderItemRepository.save(item);
            order.getItems().add(item);
        }

        if (order.getStatus() == Order.OrderStatus.served) {
            order.setStatus(Order.OrderStatus.in_kitchen);
            orderRepository.save(order);
        }

        // Devolvemos la lista de comandas activas de esa mesa actualizada
        return getActiveOrdersByTable(order.getDiningTable().getTableNumber());
    }

    @Transactional
    public void removeOrderItem(Integer itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Error: El ítem " + itemId + " no existe."));

        // Lo borramos físicamente de la base de datos
        orderItemRepository.delete(item);
    }
}