package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Order;
import com.inn.cafe.POJO.OrderItem;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.OrderDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.dto.OrderDto;
import com.inn.cafe.dto.OrderItemDto;
import com.inn.cafe.dto.BillDto;
import com.inn.cafe.dto.AddressDto;
import com.inn.cafe.POJO.Address;
import com.inn.cafe.POJO.Cart;
import com.inn.cafe.dao.CartDao;
import com.inn.cafe.exception.BaseException;
import com.inn.cafe.service.OrderService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    UserDao userDao;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    CartDao cartDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = stringRedisTemplate.opsForHash();
    }

    @Override
    @Transactional
    public ResponseEntity<String> createOrder(OrderDto orderDto) {
        log.info("Inside createOrder");
        try {
            Order order = new Order();
            order.setUuid(UUID.randomUUID().toString());
            order.setName(orderDto.getName());
            order.setEmail(orderDto.getEmail());
            order.setContactNumber(orderDto.getContactNumber());
            order.setPaymentMethod(orderDto.getPaymentMethod());
            order.setTotal(orderDto.getTotal());

            String currentUserEmail = jwtFilter.getUser();
            User user = userDao.findByEmailId(currentUserEmail);
            if(user == null) {
                 return CafeUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
            }
            order.setUser(user);

            List<OrderItem> orderItems = new ArrayList<>();
            if(orderDto.getOrderItems() != null) {
                for(OrderItemDto itemDto : orderDto.getOrderItems()) {
                    Optional<Product> productOpt = productDao.findById(itemDto.getProductId());
                    if(productOpt.isPresent()) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(order);
                        orderItem.setProduct(productOpt.get());
                        orderItem.setQuantity(itemDto.getQuantity());
                        orderItem.setPrice(itemDto.getPrice());
                        orderItems.add(orderItem);
                    } else {
                        return CafeUtils.getResponseEntity("Product not found with id: " + itemDto.getProductId(), HttpStatus.BAD_REQUEST);
                    }
                }
            }
            order.setOrderItems(orderItems);
            orderDao.save(order);
            
            return CafeUtils.getResponseEntity("Order Created Successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrders() {
        log.info("Inside getOrders");
        try {
            List<Order> orders;
            if (jwtFilter.isAdmin()) {
                orders = orderDao.getAllOrders();
            } else {
                User user = userDao.findByEmailId(jwtFilter.getUser());
                orders = orderDao.getOrderByUserId(user.getId());
            }
            
            List<OrderDto> orderDtos = orders.stream().map(this::mapToDto).collect(Collectors.toList());
            return new ResponseEntity<>(orderDtos, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> delete(Integer id) {
        log.info("Inside delete");
        try {
            if (jwtFilter.isAdmin()) {
                Optional<Order> optional = orderDao.findById(id);
                if (optional.isPresent()) {
                    orderDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Order is deleted successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Order id doesn't exist", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUuid(order.getUuid());
        dto.setName(order.getName());
        dto.setEmail(order.getEmail());
        dto.setContactNumber(order.getContactNumber());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotal(order.getTotal());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);

        if(order.getOrderItems() != null) {
            List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setOrderItems(itemDtos);
        }
        return dto;
    }

    @Override
    @Transactional
    public ResponseEntity<BillDto> checkout() throws Exception {
        String currentUserEmail = jwtFilter.getUser();
        User user = userDao.findByEmailId(currentUserEmail);
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        String redisKey = "cart:" + user.getId();
        Map<String, String> redisCart = hashOperations.entries(redisKey);

        if (redisCart == null || redisCart.isEmpty()) {
            throw new BaseException("Cart is empty", HttpStatus.BAD_REQUEST.value());
        }

        Map<String, Integer> productQuantities = new HashMap<>();
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setName(user.getName());
        order.setEmail(user.getEmail());
        order.setContactNumber(user.getContactNumber());
        order.setPaymentMethod("Cash");
        order.setUser(user);

        for (Map.Entry<String, String> entry : redisCart.entrySet()) {
            Integer productId = Integer.parseInt(entry.getKey());
            Integer quantity = Integer.parseInt(entry.getValue());

            Optional<Product> pOpt = productDao.findById(productId);
            if (pOpt.isPresent()) {
                Product p = pOpt.get();
                productQuantities.put(p.getName(), quantity);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(p);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(p.getPrice());
                orderItems.add(orderItem);

                totalAmount += (p.getPrice() * quantity);
            }
        }

        order.setTotal((int) totalAmount);
        order.setOrderItems(orderItems);
        orderDao.save(order);

        stringRedisTemplate.delete(redisKey);

        List<Cart> activeCarts = cartDao.findByUserIdAndStatus(user.getId(), "ACTIVE");
        for (Cart c : activeCarts) {
            c.setStatus("0");
            cartDao.save(c);
        }

        AddressDto activeAddressDto = null;
        if (user.getAddresses() != null) {
            for (Address a : user.getAddresses()) {
                if (a.getRecentlyUsed() != null && a.getRecentlyUsed() == 1) {
                    activeAddressDto = new AddressDto(a.getId(), a.getStreetName(), a.getLine2(), a.getLine3(), a.getPincode(), a.getCity(), a.getState(), a.getCountry(), a.getAddressName(), a.getRecentlyUsed());
                    break;
                }
            }
        }

        BillDto billDto = new BillDto(productQuantities, totalAmount, activeAddressDto);
        return new ResponseEntity<>(billDto, HttpStatus.OK);
    }
}
