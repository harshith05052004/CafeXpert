package com.inn.cafe.dao;

import com.inn.cafe.POJO.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartDao extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);
    Cart findByUserIdAndProductIdAndStatus(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("status") String status);
}
