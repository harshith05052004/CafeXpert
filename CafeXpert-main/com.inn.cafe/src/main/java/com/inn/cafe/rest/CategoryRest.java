package com.inn.cafe.rest;

import com.inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inn.cafe.dto.CategoryDto;
import com.inn.cafe.dto.ProductDto;

import java.util.List;

@RequestMapping(path = "/category")
public interface CategoryRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewCategory(@RequestBody(required = true) CategoryDto requestMap) throws Exception;

    @GetMapping(path = "/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue) throws Exception;

    @PostMapping(path = "/update")
    ResponseEntity<String> updateCategory(@RequestBody(required = true) CategoryDto requestMap) throws Exception;

    @PostMapping(path = "/getProductList")
    ResponseEntity<List<ProductDto>> getProductList(@RequestParam(required = true) String category) throws Exception;
}
