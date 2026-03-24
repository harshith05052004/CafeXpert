package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Cart;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.CartDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.dto.CartDto;
import com.inn.cafe.exception.BaseException;
import com.inn.cafe.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartDao cartDao;
    
    @Autowired
    ProductDao productDao;

    @Autowired
    UserDao userDao;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = stringRedisTemplate.opsForHash();
    }

    @Override
    @Transactional
    public ResponseEntity<CartDto> addCart(Integer productId, Integer quantity) throws Exception {
        String currentUserEmail = jwtFilter.getUser();
        User user = userDao.findByEmailId(currentUserEmail);
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        Optional<Product> optionalProduct = productDao.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new BaseException("Product not found", HttpStatus.BAD_REQUEST.value());
        }

        String redisKey = "cart:" + user.getId();
        String productIdStr = String.valueOf(productId);

        // 1. Update Redis
        String existingQtyStr = hashOperations.get(redisKey, productIdStr);
        int newQty = existingQtyStr != null ? Integer.parseInt(existingQtyStr) + quantity : quantity;
        
        if (newQty <= 0) {
            hashOperations.delete(redisKey, productIdStr);
        } else {
            hashOperations.put(redisKey, productIdStr, String.valueOf(newQty));
        }

        // 2. Update DB
        Cart cart = cartDao.findByUserIdAndProductIdAndStatus(user.getId(), productId, "ACTIVE");
        if (cart == null && newQty > 0) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(optionalProduct.get());
            cart.setQuantity(newQty);
            cart.setStatus("ACTIVE");
            cartDao.save(cart);
        } else if (cart != null) {
            if (newQty <= 0) {
                cartDao.delete(cart);
            } else {
                cart.setQuantity(newQty);
                cartDao.save(cart);
            }
        }

        // 3. Return CartDto fetched from redis
        return getCart();
    }

    @Override
    public ResponseEntity<CartDto> getCart() throws Exception {
        String currentUserEmail = jwtFilter.getUser();
        User user = userDao.findByEmailId(currentUserEmail);
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }
        
        String redisKey = "cart:" + user.getId();
        Map<String, String> redisCart = hashOperations.entries(redisKey);

        if (redisCart == null || redisCart.isEmpty()) {
            List<Cart> activeCarts = cartDao.findByUserIdAndStatus(user.getId(), "ACTIVE");
            for (Cart c : activeCarts) {
                hashOperations.put(redisKey, String.valueOf(c.getProduct().getId()), String.valueOf(c.getQuantity()));
            }
            redisCart = hashOperations.entries(redisKey);
        }

        Map<String, Integer> responseMap = new HashMap<>();
        if (redisCart != null) {
            for (Map.Entry<String, String> entry : redisCart.entrySet()) {
                Optional<Product> p = productDao.findById(Integer.parseInt(entry.getKey()));
                p.ifPresent(product -> responseMap.put(product.getName(), Integer.parseInt(entry.getValue())));
            }
        }

        return new ResponseEntity<>(new CartDto(responseMap), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<CartDto> deleteCart(Integer productId) throws Exception {
        String currentUserEmail = jwtFilter.getUser();
        User user = userDao.findByEmailId(currentUserEmail);
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        String redisKey = "cart:" + user.getId();
        String productIdStr = String.valueOf(productId);

        hashOperations.delete(redisKey, productIdStr);

        Cart cart = cartDao.findByUserIdAndProductIdAndStatus(user.getId(), productId, "ACTIVE");
        if (cart != null) {
            cartDao.delete(cart);
        }

        return getCart();
    }
}
