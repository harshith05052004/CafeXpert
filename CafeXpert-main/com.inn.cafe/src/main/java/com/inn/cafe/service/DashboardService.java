package com.inn.cafe.service;

import org.springframework.http.ResponseEntity;
import com.inn.cafe.dto.DashboardDto;

public interface DashboardService {

    ResponseEntity<DashboardDto> getDashboardDetails();
}