package com.inn.cafe.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer price;

    public OrderItemDto() {}
}
