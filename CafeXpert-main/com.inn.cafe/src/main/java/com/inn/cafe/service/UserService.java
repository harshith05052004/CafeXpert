package com.inn.cafe.service;

import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import com.inn.cafe.dto.UserDto;
import com.inn.cafe.dto.ChangePasswordDto;

import java.util.List;

public interface UserService {

    public ResponseEntity<String> signUp(UserDto requestMap) throws Exception;

    public ResponseEntity<String> login(UserDto requestMap) throws Exception;

    public ResponseEntity<List<UserWrapper>> getAllUser() throws Exception;

    public ResponseEntity<String> update(UserDto requestMap) throws Exception;

    public ResponseEntity<String> checkToken() throws Exception;

    public ResponseEntity<String> changePassword(ChangePasswordDto requestMap) throws Exception;

    public ResponseEntity<String> generateOtp(UserDto requestMap) throws Exception;

    public ResponseEntity<String> verifyOtp(UserDto requestMap) throws Exception;

    public ResponseEntity<String> setPassword(UserDto requestMap) throws Exception;

    public ResponseEntity<String> forgotPassword(UserDto requestMap) throws Exception;
}
