package com.example.order.domain.repository;


import com.example.order.domain.core.root.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, String> {
}
