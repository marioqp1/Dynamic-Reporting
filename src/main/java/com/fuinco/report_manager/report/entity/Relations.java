package com.fuinco.report_manager.report.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Relations {
    private List<String> Vehicle;
    private List<String> Position;
    private List<String> VehicleRelation = new ArrayList<>();
    private List<String> PositionRelation = new ArrayList<>();
    private HashMap<String, String> VehiclesHash = new HashMap<>();
    private HashMap<String, String> PositionHash = new HashMap<>();

    public Relations() {
        this.VehicleRelation.add("Position");
        this.PositionRelation.add("Vehicle");
        List<String> vehicles = new ArrayList<>();
        vehicles.add("id");
        vehicles.add("name");
        this.Vehicle = vehicles;
        List<String> positions = new ArrayList<>();
        positions.add("id");
        positions.add("vehicleId");
        positions.add("lon");
        positions.add("lat");
        this.Position = positions;
        this.PositionHash.put("Vehicle", "VehicleId");


    }

}
