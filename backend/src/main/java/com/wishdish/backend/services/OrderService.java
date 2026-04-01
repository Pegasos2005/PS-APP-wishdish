package com.wishDishDevelops.backend.services;

import com.wishDishDevelops.backend.models.Order;
import com.wishDishDevelops.backend.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Repositorios
import com.wishDishDevelops.backend.repositories.OrderRepository;
import com.wishDishDevelops.backend.repositories.ProductRepository;
import com.wishDishDevelops.backend.repositories.DiningTableRepository;

import java.util.List;

@Service
public class OrderService {

    // Para guardar la comanda en la tabla 'orders'
    @Autowired
    private OrderRepository orderRepository;

    // Para buscar los platos que el cliente ha elegido
    @Autowired
    private ProductRepository productRepository;

    // Para verificar que la mesa existe en la tabla 'dining_tables'
    @Autowired
    private DiningTableRepository diningTableRepository;


    // Recibe el ID de la mesa y la lista de IDs de los productos
    public Order createOrder(Integer tableId, List<Integer> productIds) {
        // Busca la mesa (con validación)
        var mesa = diningTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Error: La mesa " + tableId + " no existe."));

        // Crea la comanda
        Order nuevaComanda = new Order();
        nuevaComanda.setDiningTable(mesa);

        // Aplica el Enum que te pedía IntelliJ
        nuevaComanda.setStatus(Order.OrderStatus.valueOf("PENDIENTE"));

        // Guarda en MySQL
        return orderRepository.save(nuevaComanda);
    }

    // Devuelve las comandas que no estén pagadas ni terminadas
    public List<Order> getActiveOrders() {
        return null; // TODO: Implementar luego
    }

    // Avanza el estado de un plato y comprueba si la comanda entera ya está lista
    public OrderItem advanceItemStatus(Integer itemId) {
        return null; // TODO: Implementar luego
    }
}