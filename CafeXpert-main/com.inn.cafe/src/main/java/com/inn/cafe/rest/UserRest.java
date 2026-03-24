package com.inn.cafe.rest;

import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inn.cafe.dto.ChangePasswordDto;
import com.inn.cafe.dto.UserDto;

import java.util.List;

@RequestMapping(path = "/user")
public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUser() throws Exception;

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @GetMapping(path = "/checkToken")
    public ResponseEntity<String> checkToken() throws Exception;

    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody(required = true) ChangePasswordDto requestMap) throws Exception;

    @PostMapping(path = "/generateOtp")
    public ResponseEntity<String> generateOtp(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @PostMapping(path = "/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @PostMapping(path = "/setPassword")
    public ResponseEntity<String> setPassword(@RequestBody(required = true) UserDto requestMap) throws Exception;

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody(required = true) UserDto requestMap) throws Exception;
}
