package com.fuinco.report_manager.service;

import com.fuinco.report_manager.dto.ApiResponse;
import com.fuinco.report_manager.model.Position;
import com.fuinco.report_manager.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public ApiResponse<Position> addPosition(Position position) {
        ApiResponse<Position> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Success");
        apiResponse.setStatusCode(200);
        apiResponse.setEntity(positionRepository.save(position));
        return apiResponse;
    }

    public ApiResponse<List<Position>> getPositionByVehicleId(String id) {
        ApiResponse<List<Position>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Success");
        apiResponse.setStatusCode(200);
        apiResponse.setEntity(positionRepository.findAllByVehicleId(id));
        return apiResponse;
    }
}
