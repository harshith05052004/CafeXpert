package com.inn.cafe.service;

import com.inn.cafe.dto.AddressDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface AddressService {
    ResponseEntity<String> addAddress(AddressDto addressDto, String name) throws Exception;
    ResponseEntity<List<AddressDto>> fetchAddresses() throws Exception;
    ResponseEntity<String> selectAddress(String name) throws Exception;
    ResponseEntity<String> deleteAddress(Integer id) throws Exception;
}
