package com.inn.cafe.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private Integer id;
    private String uuid;
    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Integer total;
    private Integer userId;
    private List<OrderItemDto> orderItems;

    public OrderDto() {}
}
