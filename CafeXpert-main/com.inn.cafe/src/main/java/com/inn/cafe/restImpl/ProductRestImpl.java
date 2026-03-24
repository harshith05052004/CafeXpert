package com.inn.cafe.restImpl;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.rest.ProductRest;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductRestImpl implements ProductRest {

    @Autowired
    ProductService productService;

import com.inn.cafe.dto.ProductDto;

    @Override
    public ResponseEntity<String> addNewProduct(ProductDto requestMap) throws Exception {
        try{
            return productService.addNewProduct(requestMap);
        } catch (Exception e){
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() throws Exception {
        try{
            return productService.getAllProduct();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(ProductDto requestMap) throws Exception {
        try{
            return productService.updateProduct(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) throws Exception {
        try{
            return productService.deleteProduct(id);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Integer id, String status) throws Exception {
        try{
            return productService.updateStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer id) throws Exception {
        try{
            return productService.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<com.inn.cafe.dto.ProductDto>> searchProductsByName(String name) throws Exception {
        try {
            return productService.searchProductsByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
