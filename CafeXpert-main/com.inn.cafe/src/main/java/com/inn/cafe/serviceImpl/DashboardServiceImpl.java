package com.inn.cafe.serviceImpl;

import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.dao.OrderDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    OrderDao orderDao;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        System.out.println("inside getCount");

        Map<String , Object> map = new HashMap<>();
        map.put("category" , categoryDao.count());
        map.put("product" , productDao.count());
        map.put("order" , orderDao.count());
        return new ResponseEntity<>(map , HttpStatus.OK);
    }
}