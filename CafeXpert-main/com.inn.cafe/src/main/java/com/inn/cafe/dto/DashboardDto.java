package com.inn.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private List<CategoryProductDto> categories;
    private AddressDto defaultAddress;
    private List<ProductDto> orderAgain;
    private List<OrderItemDto> recentOrder;
}
