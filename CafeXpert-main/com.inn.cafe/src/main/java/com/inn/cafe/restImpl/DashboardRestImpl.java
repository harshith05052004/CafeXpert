package com.inn.cafe.restImpl;

import com.inn.cafe.rest.DashboardRest;
import com.inn.cafe.service.DashboardService;
import com.inn.cafe.dto.DashboardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardRestImpl implements DashboardRest {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<DashboardDto> getDashboardDetails() {
        return dashboardService.getDashboardDetails();
    }
}
