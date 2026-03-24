package com.inn.cafe.dao;

import com.inn.cafe.POJO.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDao extends JpaRepository<Order, Integer> {
    List<Order> getAllOrders();
    List<Order> getOrderByUserId(@Param("userId") Integer userId);
}
