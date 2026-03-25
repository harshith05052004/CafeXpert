package com.inn.cafe.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtils {

    private CafeUtils(){}

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus http){
        return new ResponseEntity<>("{\"message\":\""+message+"\"}", http);
    }

    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist {}" , path);
        try {
            File file = new File(path);
            return (file != null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static String getUUID(){
        Date data = new Date();
        long time =  data.getTime();
        return "BILL" + time;
    }
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String , Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data , new TypeToken<Map<String , Object>>(){
            }.getType());
        return new HashMap<>();
    }

    public static java.util.List<com.inn.cafe.dto.ProductDto> getOrderAgainDtos(java.util.List<com.inn.cafe.POJO.Product> products) {
        java.util.List<com.inn.cafe.dto.ProductDto> dtos = new java.util.ArrayList<>();
        if (products != null) {
            for (com.inn.cafe.POJO.Product p : products) {
                dtos.add(new com.inn.cafe.dto.ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCategory().getName(), p.getCategory().getId(), p.getStatus()));
            }
        }
        return dtos;
    }

    public static java.util.List<com.inn.cafe.dto.OrderItemDto> getRecentOrderDtos(com.inn.cafe.POJO.Order recentOrder) {
        java.util.List<com.inn.cafe.dto.OrderItemDto> dtos = new java.util.ArrayList<>();
        if (recentOrder != null && recentOrder.getOrderItems() != null) {
            for (com.inn.cafe.POJO.OrderItem item : recentOrder.getOrderItems()) {
                com.inn.cafe.dto.OrderItemDto dto = new com.inn.cafe.dto.OrderItemDto();
                dto.setId(item.getId());
                dto.setProductId(item.getProduct().getId());
                dto.setProductName(item.getProduct().getName());
                dto.setQuantity(item.getQuantity());
                dto.setPrice(item.getPrice());
                dtos.add(dto);
            }
        }
        return dtos;
    }

}
