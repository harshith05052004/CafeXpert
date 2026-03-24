package com.inn.cafe.restImpl;

import com.inn.cafe.exception.BaseException;
import com.inn.cafe.rest.CartRest;
import com.inn.cafe.service.CartService;
import com.inn.cafe.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartRestImpl implements CartRest {

    @Autowired
    CartService cartService;

    @Override
    public ResponseEntity<CartDto> addCart(Integer productId, Integer quantity) throws Exception {
        try {
            return cartService.addCart(productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<CartDto> getCart() throws Exception {
        try {
            return cartService.getCart();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<CartDto> deleteCart(Integer productId) throws Exception {
        try {
            return cartService.deleteCart(productId);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
