package com.inn.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDto {
    private Map<String, Integer> items;
    private Double totalAmount;
    private AddressDto address;
}
