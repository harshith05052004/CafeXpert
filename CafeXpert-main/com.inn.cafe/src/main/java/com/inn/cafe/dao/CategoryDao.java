package com.inn.cafe.dao;

import com.inn.cafe.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.inn.cafe.dto.ProductDto;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();

     @Query(name = "Category.getByCategory")
    List<ProductDto> getByCategory(@Param("name") String name);
}
