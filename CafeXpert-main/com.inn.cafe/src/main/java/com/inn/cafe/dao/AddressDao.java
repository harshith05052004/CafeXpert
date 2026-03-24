package com.inn.cafe.dao;

import com.inn.cafe.POJO.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressDao extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(@Param("userId") Integer userId);
    Address findByUserIdAndAddressName(@Param("userId") Integer userId, @Param("addressName") String addressName);
}
