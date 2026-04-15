package com.wishdish.repositories;

import com.wishdish.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByStatusNot(Order.OrderStatus status);

    List<Order> findByStatusIn(List<Order.OrderStatus> statuses);

    List<Order> findByDiningTableIdAndStatusIn(Integer tableNumber, List<Order.OrderStatus> statuses);
}