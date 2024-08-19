package com.fuinco.report_manager.controller;

import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.model.Position;
import com.fuinco.report_manager.model.Vehicle;
import com.fuinco.report_manager.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;
    @PostMapping("")
    public ApiResponse<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }
    @PostMapping("/position")
    public Position addPosition(@RequestBody Position position) {
        return vehicleService.createPosition(position);
    }
}
