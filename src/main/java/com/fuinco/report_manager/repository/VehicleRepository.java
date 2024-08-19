package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaSpecificationExecutor<Vehicle>, JpaRepository<Vehicle, Integer> {
}
