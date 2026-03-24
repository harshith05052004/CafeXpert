package com.inn.cafe.rest;

import com.inn.cafe.dto.AddressDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping(path = "/address")
public interface AddressRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addAddress(@RequestBody AddressDto addressDto, @RequestParam String name) throws Exception;

    @GetMapping(path = "/fetch")
    ResponseEntity<List<AddressDto>> fetchAddresses() throws Exception;

    @PostMapping(path = "/select")
    ResponseEntity<String> selectAddress(@RequestParam String name) throws Exception;

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteAddress(@PathVariable Integer id) throws Exception;
}
