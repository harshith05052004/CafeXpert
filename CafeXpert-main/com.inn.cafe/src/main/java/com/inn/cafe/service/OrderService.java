package com.inn.cafe.service;

import com.inn.cafe.dto.OrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    ResponseEntity<String> createOrder(OrderDto orderDto);
    ResponseEntity<List<OrderDto>> getOrders();
    ResponseEntity<String> delete(Integer id);
}
