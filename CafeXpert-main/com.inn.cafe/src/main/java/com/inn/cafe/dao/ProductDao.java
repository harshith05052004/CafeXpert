package com.inn.cafe.dao;

import com.inn.cafe.POJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<ProductWrapper> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status")String status, @Param("id") Integer id);

    ProductWrapper getProductById(@Param("id") Integer id);

    @Query(name = "Product.searchProductsByName")
    List<com.inn.cafe.dto.ProductDto> searchProductsByName(@Param("name") String name);

    @Query(value = "SELECT p.* FROM product p JOIN order_item oi ON p.id = oi.product_fk JOIN cafex_order o ON o.id = oi.order_fk WHERE o.user_fk = :userId GROUP BY p.id ORDER BY SUM(oi.quantity) DESC LIMIT 4", nativeQuery = true)
    List<Product> findTop4OrderedProductsByUser(@Param("userId") Integer userId);
}
