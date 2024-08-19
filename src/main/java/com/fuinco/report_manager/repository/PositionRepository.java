package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.model.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends MongoRepository<Position, Integer> {
    List<Position> findAllByVehicleId(String vehicleId);
}
