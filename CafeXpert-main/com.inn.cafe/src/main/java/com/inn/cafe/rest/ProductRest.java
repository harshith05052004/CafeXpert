package com.inn.cafe.rest;

import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

import com.inn.cafe.dto.ProductDto;

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody ProductDto productDto) throws Exception;

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProduct() throws Exception;

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody ProductDto productDto) throws Exception;

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id) throws Exception;

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus(@RequestParam("id") Integer id, @RequestParam("status") String status) throws Exception;

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProductWrapper> getById(@PathVariable Integer id) throws Exception;

    @GetMapping(path = "/search/{name}")
    ResponseEntity<List<com.inn.cafe.dto.ProductDto>> searchProductsByName(@PathVariable String name);
}
