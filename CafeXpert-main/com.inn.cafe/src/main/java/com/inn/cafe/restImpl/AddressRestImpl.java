package com.inn.cafe.restImpl;

import com.inn.cafe.dto.AddressDto;
import com.inn.cafe.exception.BaseException;
import com.inn.cafe.rest.AddressRest;
import com.inn.cafe.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AddressRestImpl implements AddressRest {

    @Autowired
    AddressService addressService;

    @Override
    public ResponseEntity<String> addAddress(AddressDto addressDto, String name) throws Exception {
        try {
            return addressService.addAddress(addressDto, name);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<List<AddressDto>> fetchAddresses() throws Exception {
        try {
            return addressService.fetchAddresses();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<String> selectAddress(String name) throws Exception {
        try {
            return addressService.selectAddress(name);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<String> deleteAddress(Integer id) throws Exception {
        try {
            return addressService.deleteAddress(id);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
