package com.fuinco.report_manager.service;

import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.model.Position;
import com.fuinco.report_manager.model.Vehicle;
import com.fuinco.report_manager.repository.PositionRepository;
import com.fuinco.report_manager.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final PositionRepository positionRepository;

    public VehicleService(VehicleRepository vehicleRepository, PositionRepository positionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.positionRepository = positionRepository;
    }

    public ApiResponse<Vehicle> getVehicleById(int id) {
        ApiResponse<Vehicle> apiResponse = new ApiResponse<>();
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            apiResponse.setEntity(vehicle.get());
            apiResponse.setMessage("Vehicle found");
            apiResponse.setStatusCode(200);
            apiResponse.setSuccess(true);
        } else {
            apiResponse.setStatusCode(404);
            apiResponse.setMessage("Vehicle not found");
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }

    public ApiResponse<Vehicle> addVehicle(Vehicle vehicle) {
        ApiResponse<Vehicle> apiResponse = new ApiResponse<>();
        if (vehicle.getName() != null && !(vehicle.getName().isEmpty())) {
            apiResponse.setEntity(vehicleRepository.save(vehicle));
            apiResponse.setMessage("Vehicle added");
            apiResponse.setStatusCode(200);
            apiResponse.setSuccess(true);
        } else {
            apiResponse.setSuccess(false);
            apiResponse.setStatusCode(500);
            apiResponse.setMessage("Vehicle cannot be added");
        }
        return apiResponse;
    }

    public ApiResponse<Vehicle> updateVehicle(Vehicle vehicle) {
        ApiResponse<Vehicle> apiResponse = new ApiResponse<>();
        if (vehicleRepository.existsById(vehicle.getId())) {
            if (vehicle.getName() != null && !(vehicle.getName().isEmpty())) {
                apiResponse.setEntity(vehicleRepository.save(vehicle));
                apiResponse.setMessage("Vehicle updated");
                apiResponse.setStatusCode(200);
                apiResponse.setSuccess(true);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setStatusCode(500);
                apiResponse.setMessage("Vehicle cannot be updated " + "Name is empty");
            }
        } else {
            apiResponse.setStatusCode(404);
            apiResponse.setMessage("Vehicle not found");
            apiResponse.setSuccess(false);
        }

        return apiResponse;
    }
    public Position createPosition(Position position) {
        return positionRepository.save(position);
    }
}
