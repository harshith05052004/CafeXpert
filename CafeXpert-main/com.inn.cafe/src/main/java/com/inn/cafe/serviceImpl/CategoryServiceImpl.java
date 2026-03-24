package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inn.cafe.exception.BaseException;

import com.inn.cafe.dto.CategoryDto;
import com.inn.cafe.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(CategoryDto requestMap) throws Exception {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
                }
            }
            else{
                return CafeUtils.getResponseEntity("Unauthorized Access", HttpStatus.UNAUTHORIZED);
            }

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
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) throws Exception {
        try{
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside if");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }

            return new ResponseEntity<List<Category>>(categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(CategoryDto requestMap) throws Exception {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDao.findById(requestMap.getId());
                    if(!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Category Updated Successfully", HttpStatus.OK);
                    }else{
                        return CafeUtils.getResponseEntity("Category Id doesn't exists", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity("Invalid Data", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(CategoryDto requestMap, boolean validateId) {
        if(requestMap.getName() != null && !requestMap.getName().isEmpty()){
            if(requestMap.getId() != null && validateId){
                return true;
            }
            else if(!validateId){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(CategoryDto requestMap,boolean isAdd){
        Category category = new Category();

        if(isAdd){
            category.setId(requestMap.getId());
        }

        category.setName(requestMap.getName());
        return category;
    }

    @Override
    public ResponseEntity<List<ProductDto>> getProductList(String category) throws Exception {
        try{
            return new ResponseEntity<>(categoryDao.getByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<List<ProductDto>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
        
}
