package com.inn.cafe.service;

import com.inn.cafe.POJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;


public interface ProductService {

import com.inn.cafe.dto.ProductDto;

    ResponseEntity<String> addNewProduct(ProductDto requestMap) throws Exception;

    ResponseEntity<List<ProductWrapper>> getAllProduct() throws Exception;

    ResponseEntity<String> updateProduct(ProductDto requestMap) throws Exception;

    ResponseEntity<String> deleteProduct(Integer id) throws Exception;

    ResponseEntity<ProductWrapper> getById(Integer id);

    ResponseEntity<String> updateStatus(Integer id, String status) throws Exception;

    ResponseEntity<List<com.inn.cafe.dto.ProductDto>> searchProductsByName(String name) throws Exception;
}
