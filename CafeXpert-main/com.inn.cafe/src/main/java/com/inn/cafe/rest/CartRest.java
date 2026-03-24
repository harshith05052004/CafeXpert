package com.inn.cafe.rest;

import com.inn.cafe.dto.CartDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/cart")
public interface CartRest {

    @PostMapping(path = "/add")
    ResponseEntity<CartDto> addCart(@RequestParam Integer productId, @RequestParam Integer quantity) throws Exception;
    
    @GetMapping(path = "/get")
    ResponseEntity<CartDto> getCart() throws Exception;

    @PostMapping(path = "/delete/{productId}")
    ResponseEntity<CartDto> deleteCart(@PathVariable Integer productId) throws Exception;
}
