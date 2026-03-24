package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserServiceDetails;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.inn.cafe.dto.UserDto;
import com.inn.cafe.dto.ChangePasswordDto;
import com.inn.cafe.exception.BaseException;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserServiceDetails service;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> signUp(UserDto requestMap) throws Exception {
        log.info("Inside signUp {}", requestMap);
        try {
            if (validate(requestMap)) {
                User user = userDao.findByEmailId(requestMap.getEmail());
                if (Objects.isNull(user)) {
                    userDao.save(getFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exits", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
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

    private boolean validate(UserDto requestMap) {
        if (requestMap.getName() != null && requestMap.getPassword() != null && requestMap.getEmail() != null && requestMap.getContactNumber() != null) {
            return true;
        }
        return false;
    }

    private User getFromMap(UserDto requestMap){
        User user = new User();
        user.setPassword(requestMap.getPassword());
        user.setName(requestMap.getName());
        user.setEmail(requestMap.getEmail());
        user.setContactNumber(requestMap.getContactNumber());
        user.setRole("user");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> login(UserDto requestMap) throws Exception {
        log.info("Inside login");
        try{
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.getEmail(), requestMap.getPassword()));
            if(auth.isAuthenticated()){
                if(service.getDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(service.getDetail().getEmail(), service.getDetail().getRole()) + "\"}", HttpStatus.OK);
                }

                else{
                    return new ResponseEntity<String>("{\"message\":\""+ "Wait for admin approval" + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return new ResponseEntity<String>("{\"message\":\""+ "Bad Credentials" + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() throws Exception {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return  new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(UserDto requestMap) throws Exception {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(requestMap.getId());
                if (!optional.isEmpty()) {

                    userDao.updateStatus(requestMap.getStatus(), requestMap.getId());
                    sendMailToAllAdmin(requestMap.getStatus(), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User Status is updated Successfully", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity("User id doesn't exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getMessage(), e.getStatusCode());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() throws Exception {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(ChangePasswordDto requestMap) throws Exception {
            try{
                User userObj = userDao.findByEmail(jwtFilter.getUser());
                if(!userObj.equals(null)){
                    if(userObj.getPassword().equals(requestMap.getOldPassword())){
                        userObj.setPassword(requestMap.getNewPassword());
                        userDao.save(userObj);
                        return CafeUtils.getResponseEntity("Successfully Changed", HttpStatus.OK);
                    }
                    return CafeUtils.getResponseEntity("Incorrect Password", HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

            }catch (Exception e){
                e.printStackTrace();
            }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(UserDto requestMap) throws Exception {
        try{
            if (Strings.isNullOrEmpty(requestMap.getEmail())) {
                return CafeUtils.getResponseEntity("Email is required", HttpStatus.BAD_REQUEST);
            }
            User existingUser = userDao.findByEmailId(requestMap.getEmail());
            if (Objects.isNull(existingUser)) {
                return CafeUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
            }
            return sendAndStoreOtp(requestMap.getEmail(), existingUser.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<String> generateOtp(UserDto requestMap) throws Exception {
        try {
            if (Strings.isNullOrEmpty(requestMap.getEmail())) {
                return CafeUtils.getResponseEntity("Email is required", HttpStatus.BAD_REQUEST);
            }
            User existingUser = userDao.findByEmailId(requestMap.getEmail());
            if (!Objects.isNull(existingUser)) {
                return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
            }
            return sendAndStoreOtp(requestMap.getEmail(), requestMap.getUserType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<String> verifyOtp(UserDto requestMap) throws Exception {
        try {
            if (Strings.isNullOrEmpty(requestMap.getEmail()) || Strings.isNullOrEmpty(requestMap.getOtp())) {
                return CafeUtils.getResponseEntity("Email and OTP are required", HttpStatus.BAD_REQUEST);
            }
            String otpKey = CafeConstants.OTP_PREFIX + requestMap.getEmail();
            String storedOtp = stringRedisTemplate.opsForValue().get(otpKey);
            
            if (storedOtp == null || !storedOtp.equals(requestMap.getOtp())) {
                return CafeUtils.getResponseEntity("OTP expired or invalid", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity("OTP verified successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<String> setPassword(UserDto requestMap) throws Exception {
        try {
            if (Strings.isNullOrEmpty(requestMap.getEmail()) || Strings.isNullOrEmpty(requestMap.getOtp()) || Strings.isNullOrEmpty(requestMap.getPassword())) {
                return CafeUtils.getResponseEntity("Email, OTP and Password are required", HttpStatus.BAD_REQUEST);
            }
            
            String otpKey = CafeConstants.OTP_PREFIX + requestMap.getEmail();
            String storedOtp = stringRedisTemplate.opsForValue().get(otpKey);
            
            if (storedOtp == null || !storedOtp.equals(requestMap.getOtp())) {
                return CafeUtils.getResponseEntity("OTP expired or invalid", HttpStatus.BAD_REQUEST);
            }
            
            User existingUser = userDao.findByEmailId(requestMap.getEmail());
            if (existingUser != null) {
                existingUser.setPassword(passwordEncoder.encode(requestMap.getPassword()));
                userDao.save(existingUser);
            } else {
                if (!validate(requestMap)) {
                   return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
                User newUser = getFromMap(requestMap);
                newUser.setPassword(passwordEncoder.encode(requestMap.getPassword()));
                userDao.save(newUser);
            }
            
            stringRedisTemplate.delete(otpKey);
            return CafeUtils.getResponseEntity("Password set successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private ResponseEntity<String> sendAndStoreOtp(String email, String userType) {
        String domain = email.substring(email.indexOf("@") + 1);
        if ("admin".equalsIgnoreCase(userType)) {
            if (!CafeConstants.ADMIN_DOMAINS.contains(domain)) {
                return CafeUtils.getResponseEntity("Invalid Admin Domain", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (!CafeConstants.NORMAL_DOMAINS.contains(domain)) {
                return CafeUtils.getResponseEntity("Invalid Email Domain", HttpStatus.BAD_REQUEST);
            }
        }

        String cooldownKey = CafeConstants.OTP_COOLDOWN_PREFIX + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(cooldownKey))) {
            return CafeUtils.getResponseEntity("Please wait before requesting another OTP", HttpStatus.TOO_MANY_REQUESTS);
        }

        String reqKey = CafeConstants.OTP_REQ_PREFIX + email;
        String reqCountStr = stringRedisTemplate.opsForValue().get(reqKey);
        int reqCount = reqCountStr != null ? Integer.parseInt(reqCountStr) : 0;

        if (reqCount >= CafeConstants.MAX_OTP_REQUESTS) {
            return CafeUtils.getResponseEntity("Maximum OTP requests exceeded. Try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        emailUtils.forgotMail(email, "Your OTP - Cafe Management System", "Your OTP is: " + otp);

        String otpKey = CafeConstants.OTP_PREFIX + email;
        stringRedisTemplate.opsForValue().set(otpKey, otp, java.time.Duration.ofSeconds(CafeConstants.OTP_TTL));
        stringRedisTemplate.opsForValue().set(cooldownKey, "true", java.time.Duration.ofSeconds(CafeConstants.COOLDOWN_TTL));
        
        if (reqCount == 0) {
            stringRedisTemplate.opsForValue().set(reqKey, "1", java.time.Duration.ofSeconds(CafeConstants.REQ_COUNT_TTL));
        } else {
            stringRedisTemplate.opsForValue().increment(reqKey);
        }

        return CafeUtils.getResponseEntity("OTP sent successfully", HttpStatus.OK);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.SendSimpleMessage(jwtFilter.getUser(), "Account Approved", "USER:- " + user + "\n is approved by\nADMIN:-" + jwtFilter.getUser(), allAdmin);
        } else {
            emailUtils.SendSimpleMessage(jwtFilter.getUser(), "Account Disabled", "USER:- " + user + "\n is disabled by\nADMIN:-" + jwtFilter.getUser(), allAdmin);

        }
    }

}
