package com.inn.cafe.rest;

import com.inn.cafe.dto.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inn.cafe.dto.BillDto;
import java.util.List;

@RequestMapping(path = "/order")
public interface OrderRest {

    @PostMapping(path = "/createOrder")
    ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto);

    @GetMapping(path = "/getOrders")
    ResponseEntity<List<OrderDto>> getOrders();

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteOrder(@PathVariable Integer id);

    @PostMapping(path = "/checkout")
    ResponseEntity<BillDto> checkout() throws Exception;
}
