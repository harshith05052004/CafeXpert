package com.inn.cafe.service;

import com.inn.cafe.dto.CartDto;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<CartDto> addCart(Integer productId, Integer quantity) throws Exception;
    ResponseEntity<CartDto> getCart() throws Exception;
    ResponseEntity<CartDto> deleteCart(Integer productId) throws Exception;
}
