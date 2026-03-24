package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.CustomerUserServiceDetails;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.constants.CafeConstants;

import com.inn.cafe.dao.ProductDao;

import com.inn.cafe.service.ProductService;

import com.inn.cafe.utils.CafeUtils;

import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import com.inn.cafe.dto.ProductDto;
import com.inn.cafe.exception.BaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    com.inn.cafe.JWT.JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CustomerUserServiceDetails customerUserDetailsService;

    @Autowired
    EmailUtils emailUtil;

    @Override
    @Transactional
    public ResponseEntity<String> addNewProduct(ProductDto requestMap) throws Exception {
        log.info("Inside addNewProduct{}", requestMap);
        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, false)) {
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        //System.out.println(CafeConstants.SOMETHING_WENT_WRONG);
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<com.inn.cafe.dto.ProductDto>> searchProductsByName(String name) throws Exception {
        try {
            // This implementation currently returns all products, as per the provided snippet.
            // A proper implementation would involve a productDao method like productDao.findByNameContaining(name)
            // and mapping the results to ProductDto.
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        // This return statement is unreachable if exceptions are always thrown or caught.
        // It's kept for consistency with the original structure if no exception is thrown.
        // return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() throws Exception {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    @Transactional
    public ResponseEntity<String> updateProduct(ProductDto requestMap) throws Exception {
        try {
            if (jwtFilter.isAdmin()) {
                // Log the incoming request map
                System.out.println("Request Map: " + requestMap);

                // Validate the product map and log the result
                boolean isValid = validateProductMap(requestMap, true);
                System.out.println("Is Valid Product Map: " + isValid);

                if (isValid) {
                    Optional<Product> optional = productDao.findById(requestMap.getId());
                    if (!optional.isEmpty()) {
                        productDao.save(getProductFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Product is updated successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
//        try {
//            if (jwtFilter.isAdmin()) {
//                if (validateProductMap(requestMap, true)) {
//                    Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
//                    if (!optional.isEmpty()) {
//                        productDao.save(getProductFromMap(requestMap, true));
//                        return CafeUtils.getResponseEntity("Product is updated successfully", HttpStatus.OK);
//
//                    } else {
//                        return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
//                    }
//
//                }
//                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
//            } else {
//                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteProduct(Integer id) throws Exception {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(id);
                if (!optional.isEmpty()) {
                    productDao.deleteById(id);
                    //System.out.println("Product is deleted successfully");
                    return CafeUtils.getResponseEntity("Product is deleted successfully", HttpStatus.OK);
                }
                //System.out.println("Product id doesn't exist");
                return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        //System.out.println(CafeConstants.SOMETHING_WENT_WRONG);
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            List<ProductWrapper> products = productDao.getByCategory(id);
            System.out.println("Fetched Products: " + products);  // Add this line
            if (products == null || products.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @Override
    public ResponseEntity<ProductWrapper> getById(Integer id) throws Exception {
        try {
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateStatus(Integer id, String status) throws Exception {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = productDao.findById(id);
                if (!optional.isEmpty()) {
                    productDao.updateProductStatus(status, id);
                    return CafeUtils.getResponseEntity("Product status is updated successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof BaseException) {
                throw (BaseException) ex;
            }
            throw new BaseException("Something went wrong: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateProductMap(ProductDto requestMap, boolean validateId) {
        // Check if the requestMap is null
        if (requestMap == null) {
            System.out.println("Request map is null.");
            return false;
        }

        // Check for the 'name' key and ensure it has a non-empty value
        String name = requestMap.getName();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Invalid or missing 'name' key.");
            return false;
        }

        // If validateId is true, check for the 'id' key and ensure it has a non-empty value
        if (validateId) {
            Integer id = requestMap.getId();
            if (id == null) {
                System.out.println("Invalid or missing 'id' key.");
                return false;
            }
        }

        // All required validations passed
        return true;
    }


    private Product getProductFromMap(ProductDto requestMap, boolean isAdd) {
        Product product = new Product();
        Category category = new Category();
        category.setId(requestMap.getCategoryId());

        if (isAdd) {
            product.setId(requestMap.getId());
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.getName());
        product.setDescription(requestMap.getDescription());
        product.setPrice(requestMap.getPrice());
        product.setStatus(String.valueOf(isAdd));

        return product;
    }
}