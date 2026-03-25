package com.inn.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProductDto {
    private Integer categoryId;
    private String categoryName;
    private List<ProductDto> products;
}
