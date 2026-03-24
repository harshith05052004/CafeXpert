package com.inn.cafe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {

    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String categoryName;
    private Integer categoryId;
    private String status;

    public ProductDto(Integer id, String name, String description, Integer price, String categoryName, Integer categoryId, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.status = status;
    }

    public ProductDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
