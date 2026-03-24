package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Address;
import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.AddressDao;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.dto.AddressDto;
import com.inn.cafe.exception.BaseException;
import com.inn.cafe.service.AddressService;
import com.inn.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressDao addressDao;

    @Autowired
    UserDao userDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    @Transactional
    public ResponseEntity<String> addAddress(AddressDto addressDto, String name) throws Exception {
        User user = userDao.findByEmailId(jwtFilter.getUser());
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        List<Address> existing = addressDao.findByUserId(user.getId());
        for (Address a : existing) {
            if (a.getRecentlyUsed() != null && a.getRecentlyUsed() == 1) {
                a.setRecentlyUsed(0);
                addressDao.save(a);
            }
        }

        Address address = new Address();
        address.setStreetName(addressDto.getStreetName());
        address.setLine2(addressDto.getLine2());
        address.setLine3(addressDto.getLine3());
        address.setPincode(addressDto.getPincode());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setAddressName(name);
        address.setRecentlyUsed(1);
        address.setUser(user);

        addressDao.save(address);

        return CafeUtils.getResponseEntity("Address added successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AddressDto>> fetchAddresses() throws Exception {
        User user = userDao.findByEmailId(jwtFilter.getUser());
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        List<Address> addresses = addressDao.findByUserId(user.getId());
        List<AddressDto> addressDtos = new ArrayList<>();
        for (Address a : addresses) {
            addressDtos.add(new AddressDto(a.getId(), a.getStreetName(), a.getLine2(), a.getLine3(), a.getPincode(), a.getCity(), a.getState(), a.getCountry(), a.getAddressName(), a.getRecentlyUsed()));
        }

        return new ResponseEntity<>(addressDtos, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<String> selectAddress(String name) throws Exception {
        User user = userDao.findByEmailId(jwtFilter.getUser());
        if (user == null) {
            throw new BaseException("User not found", HttpStatus.BAD_REQUEST.value());
        }

        List<Address> existing = addressDao.findByUserId(user.getId());
        boolean found = false;
        for (Address a : existing) {
            if (a.getAddressName() != null && a.getAddressName().equalsIgnoreCase(name)) {
                a.setRecentlyUsed(1);
                found = true;
            } else {
                a.setRecentlyUsed(0);
            }
            addressDao.save(a);
        }

        if (!found) {
            throw new BaseException("Address name not found", HttpStatus.BAD_REQUEST.value());
        }

        return CafeUtils.getResponseEntity("Address selected successfully.", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteAddress(Integer id) throws Exception {
        Optional<Address> opt = addressDao.findById(id);
        if (opt.isPresent()) {
            addressDao.deleteById(id);
            return CafeUtils.getResponseEntity("Address deleted successfully.", HttpStatus.OK);
        }
        throw new BaseException("Address id does not exist", HttpStatus.BAD_REQUEST.value());
    }
}
