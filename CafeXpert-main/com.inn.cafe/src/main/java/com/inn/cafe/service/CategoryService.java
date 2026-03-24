package com.inn.cafe.service;

import com.inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;

import com.inn.cafe.dto.CategoryDto;
import com.inn.cafe.dto.ProductDto;

import java.util.List;

public interface CategoryService {
    public ResponseEntity<String> addNewCategory(CategoryDto requestMap) throws Exception;

    public ResponseEntity<List<Category>> getAllCategory(String filterValue) throws Exception;

    public ResponseEntity<String> updateCategory(CategoryDto requestMap) throws Exception;

    public ResponseEntity<List<ProductDto>> getProductList(String category) throws Exception;
}
