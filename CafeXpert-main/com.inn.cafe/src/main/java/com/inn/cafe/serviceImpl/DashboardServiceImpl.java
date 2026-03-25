package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Address;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.AddressDao;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.dto.AddressDto;
import com.inn.cafe.dto.CategoryProductDto;
import com.inn.cafe.dto.DashboardDto;
import com.inn.cafe.dto.ProductDto;
import com.inn.cafe.service.DashboardService;
import com.inn.cafe.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AddressDao addressDao;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    com.inn.cafe.dao.OrderDao orderDao;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<DashboardDto> getDashboardDetails() {
        try {
            DashboardDto dashboardDto = new DashboardDto();
            
            // Get current user and address
            String currentUserEmail = jwtFilter.getUser();
            if(currentUserEmail != null) {
                User user = userDao.findByEmailId(currentUserEmail);
                if(user != null) {
                    Address address = addressDao.findByUserIdAndRecentlyUsed(user.getId(), 1);
                    if (address != null) {
                        AddressDto addressDto = new AddressDto(
                            address.getId(),
                            address.getStreetName(),
                            address.getLine2(),
                            address.getLine3(),
                            address.getPincode(),
                            address.getCity(),
                            address.getState(),
                            address.getCountry(),
                            address.getAddressName(),
                            address.getRecentlyUsed()
                        );
                        dashboardDto.setDefaultAddress(addressDto);
                    }

                    // Populate OrderAgain and RecentOrder
                    java.util.List<com.inn.cafe.POJO.Product> topProducts = productDao.findTop4OrderedProductsByUser(user.getId());
                    dashboardDto.setOrderAgain(com.inn.cafe.utils.CafeUtils.getOrderAgainDtos(topProducts));

                    com.inn.cafe.POJO.Order recentOrder = orderDao.findFirstByUserIdOrderByIdDesc(user.getId());
                    dashboardDto.setRecentOrder(com.inn.cafe.utils.CafeUtils.getRecentOrderDtos(recentOrder));
                }
            }

            // Get categories and products
            List<Category> categories = categoryDao.getAllCategory();
            List<CategoryProductDto> categoryProductDtos = new ArrayList<>();
            
            for (Category category : categories) {
                CategoryProductDto cpDto = new CategoryProductDto();
                cpDto.setCategoryId(category.getId());
                cpDto.setCategoryName(category.getName());
                
                List<ProductWrapper> productWrappers = productDao.getByCategory(category.getId());
                List<ProductDto> productDtos = new ArrayList<>();
                for (ProductWrapper pw : productWrappers) {
                    ProductDto productDto = new ProductDto(
                        pw.getId(),
                        pw.getName(),
                        pw.getDescription(),
                        pw.getPrice(),
                        pw.getCategoryName(),
                        pw.getCategoryId(),
                        pw.getStatus()
                    );
                    productDtos.add(productDto);
                }
                cpDto.setProducts(productDtos);
                categoryProductDtos.add(cpDto);
            }
            
            dashboardDto.setCategories(categoryProductDtos);

            return new ResponseEntity<>(dashboardDto, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error occurred while getting dashboard details", ex);
        }
        return new ResponseEntity<>(new DashboardDto(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}